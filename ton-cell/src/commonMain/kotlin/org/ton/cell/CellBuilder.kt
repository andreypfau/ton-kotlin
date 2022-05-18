package org.ton.cell

import org.ton.bigint.*
import org.ton.bitstring.BitString

interface CellBuilder {
    var bits: BitString
    var refs: MutableList<Cell>

    /**
     * @return the depth of builder. If no cell references are stored in builder, then returns `0`;
     * otherwise the returned value is one plus the maximum of depths of cells referred to from builder.
     */
    val depth: Int get() = refs.maxOfOrNull { it.maxDepth } ?: 0

    /**
     * Converts a builder into an ordinary cell.
     */
    fun endCell(): Cell

    fun storeBit(bit: Boolean): CellBuilder
    fun storeBits(vararg bits: Boolean): CellBuilder
    fun storeBits(bits: Iterable<Boolean>): CellBuilder

    /**
     * Stores a reference to cell into builder.
     */
    fun storeRef(ref: Cell): CellBuilder
    fun storeRef(refBuilder: CellBuilder.() -> Unit): CellBuilder

    /**
     * Stores an unsigned [length]-bit integer [value] into builder for 0 ≤ [length] ≤ 256.
     */
    fun storeUInt(value: BigInt, length: Int): CellBuilder
    fun storeUInt(value: Long, length: Int): CellBuilder = storeUInt(BigInt(value), length)
    fun storeUInt(value: Int, length: Int): CellBuilder = storeUInt(BigInt(value), length)
    fun storeUInt(value: Short, length: Int): CellBuilder = storeUInt(BigInt(value), length)
    fun storeUInt(value: Byte, length: Int): CellBuilder = storeUInt(BigInt(value), length)

    fun storeUIntLeq(value: BigInt, max: BigInt): CellBuilder = storeUInt(value, max.bitLength)
    fun storeUIntLeq(value: Long, max: Long): CellBuilder = storeUIntLeq(BigInt(value), BigInt(max))
    fun storeUIntLeq(value: Int, max: Int): CellBuilder = storeUIntLeq(BigInt(value), BigInt(max))
    fun storeUIntLeq(value: Short, max: Short): CellBuilder = storeUIntLeq(BigInt(value), BigInt(max))
    fun storeUIntLeq(value: Byte, max: Byte): CellBuilder = storeUIntLeq(BigInt(value), BigInt(max))

    fun storeUIntLes(value: BigInt, max: BigInt): CellBuilder = storeUInt(value, (max - 1).bitLength)
    fun storeUIntLes(value: Long, max: Long): CellBuilder = storeUIntLes(BigInt(value), BigInt(max))
    fun storeUIntLes(value: Int, max: Int): CellBuilder = storeUIntLes(BigInt(value), BigInt(max))
    fun storeUIntLes(value: Short, max: Short): CellBuilder = storeUIntLes(BigInt(value), BigInt(max))
    fun storeUIntLes(value: Byte, max: Byte): CellBuilder = storeUIntLes(BigInt(value), BigInt(max))

    /**
     * Stores a signed [length]-bit integer [value] into builder for 0 ≤ [length] ≤ 257.
     */
    fun storeInt(value: BigInt, length: Int): CellBuilder
    fun storeInt(value: Long, length: Int): CellBuilder = storeInt(BigInt(value), length)
    fun storeInt(value: Int, length: Int): CellBuilder = storeInt(BigInt(value), length)
    fun storeInt(value: Short, length: Int): CellBuilder = storeInt(BigInt(value), length)
    fun storeInt(value: Byte, length: Int): CellBuilder = storeInt(BigInt(value), length)

    /**
     * Stores [slice] into builder.
     */
    fun storeSlice(slice: CellSlice): CellBuilder

    companion object {
        @JvmStatic
        fun beginCell(maxLength: Int = BitString.MAX_LENGTH): CellBuilder = CellBuilderImpl(maxLength)

        @JvmStatic
        fun createCell(maxLength: Int = BitString.MAX_LENGTH, builder: CellBuilder.() -> Unit): Cell = CellBuilderImpl(maxLength).apply(builder).endCell()
    }
}

private class CellBuilderImpl(
    maxLength: Int
) : CellBuilder {
    override var bits: BitString = BitString(maxLength)
    override var refs: MutableList<Cell> = ArrayList()

    private val remainder: Int get() = bits.length - writePosition
    private var writePosition: Int = 0

    override fun endCell(): Cell = Cell(bits.slice(0 .. writePosition - 1), refs)

    override fun storeBit(bit: Boolean): CellBuilder = apply {
        checkBitsOverflow(1)
        bits[writePosition++] = bit
    }

    override fun storeBits(vararg bits: Boolean): CellBuilder = apply {
        checkBitsOverflow(bits.size)
        bits.forEach { bit ->
            this.bits[writePosition++] = bit
        }
    }

    override fun storeBits(bits: Iterable<Boolean>): CellBuilder = storeBits(*bits.toList().toBooleanArray())

    override fun storeRef(ref: Cell): CellBuilder = apply {
        checkRefsOverflow(1)
        refs.add(ref)
    }

    override fun storeRef(refBuilder: CellBuilder.() -> Unit): CellBuilder = apply {
        storeRef(CellBuilder.beginCell().apply(refBuilder).endCell())
    }

    override fun storeUInt(value: BigInt, length: Int): CellBuilder = apply {
        val bits = BooleanArray(length) { index ->
            ((value shr index) and BigInt(1)).toInt() == 1
        }.reversedArray()
        storeBits(*bits)
    }

    override fun storeInt(value: BigInt, length: Int): CellBuilder = apply {
        val intBits = BigInt(1) shl (length - 1)
        require(value >= -intBits && value < intBits) { "Can't store an Int, because its value allocates more space than provided." }
        storeInt(value, length)
    }

    override fun storeSlice(slice: CellSlice): CellBuilder = apply {
        val (bits, refs) = slice

        checkBitsOverflow(bits.length)
        checkRefsOverflow(refs.size)

        storeBits(bits)
        refs.forEach { ref ->
            storeRef(ref)
        }
    }

    private fun checkBitsOverflow(length: Int) = require(length <= remainder) {
        "Bits overflow. Can't add $length bits. $remainder bits left."
    }

    private fun checkRefsOverflow(count: Int) = require(count <= (4 - refs.size)) {
        "Refs overflow. Can't add $count refs. ${4 - refs.size} refs left."
    }
}
