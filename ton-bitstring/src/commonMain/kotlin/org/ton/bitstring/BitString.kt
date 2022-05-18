@file:Suppress("OPT_IN_USAGE")

package org.ton.bitstring

import kotlinx.serialization.Serializable
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or
import kotlin.math.min

@Serializable(with = FiftHexBitStringSerializer::class)
interface BitString : Iterable<Boolean>, Comparable<BitString> {
    val length: Int

    operator fun set(index: Int, bit: Int)
    operator fun set(index: Int, bit: Boolean)
    operator fun get(index: Int): Boolean
    fun slice(indices: IntRange) : BitString
    fun toByteArray(): ByteArray
    fun toBooleanArray(): BooleanArray
    fun isEmpty(): Boolean = length == 0

    override fun toString(): String

    class BitStringIterator(val bitString: BitString) : BooleanIterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < bitString.length
        override fun nextBoolean(): Boolean = bitString[index++]
    }

    override fun iterator(): Iterator<Boolean> = BitStringIterator(this)

    companion object {
        const val MAX_LENGTH = 1023

        @JvmStatic
        fun of(vararg bits: Boolean): BitString = BitString(*bits)

        @JvmStatic
        fun of(bits: Iterable<Boolean>): BitString = BitString(bits)

        @JvmStatic
        fun of(hex: String): BitString = BitString(hex)

        @JvmStatic
        fun of(byteArray: ByteArray): BitString = BitString(byteArray)

        @JvmStatic
        fun of(length: Int): BitString = BitString(length)
    }
}

internal class ByteArrayBitStringImpl constructor(
        override val length: Int = BitString.MAX_LENGTH,
        private val bytes: ByteArray = ByteArray(length / Byte.SIZE_BITS + if (length % Byte.SIZE_BITS == 0) 0 else 1)
) : BitString {
    init {
        checkLength()
    }

    override operator fun set(index: Int, bit: Int) = set(index, bit != 0)

    override operator fun set(index: Int, bit: Boolean) {
        val wordIndex = index.wordIndex
        val bitMask = index.bitMask
        if (bit) {
            bytes[wordIndex] = bytes[wordIndex] or bitMask
        } else {
            bytes[wordIndex] = bytes[wordIndex] and bitMask.inv()
        }
    }

    override operator fun get(index: Int): Boolean {
        val wordIndex = index.wordIndex
        val bitMask = index.bitMask
        return (bytes[wordIndex] and bitMask) != 0.toByte()
    }

    override fun slice(indices: IntRange): BitString {
        var result = ByteArrayBitStringImpl(length = indices.last - indices.first + 1)
        for ((position, i) in indices.withIndex()) {
            result.set(position, get(i))
        }
        return result
    }

    override fun toByteArray(): ByteArray = bytes.copyOf()

    override fun toBooleanArray(): BooleanArray = toList().toBooleanArray()

    override fun compareTo(other: BitString): Int {
        val limit = min(length, other.length)
        repeat(limit) {
            val thisValue = this[it]
            val otherValue = other[it]
            if (thisValue != otherValue) {
                return if (thisValue) {
                    1
                } else {
                    -1
                }
            }
        }
        return length - other.length
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        val l = length % 4
        if (l == 0) {
            bytes.forEach { byte ->
                val hex = (byte.toInt() and 0xFF).toString(16).uppercase()
                stringBuilder.append(hex)
            }
            if (length % 8 != 0) {
                stringBuilder.setLength(stringBuilder.length - 1)
            }
        } else {
            bytes.forEach { byte ->
                val hex = (byte.toInt() and 0xFF).toString(16).uppercase()
                stringBuilder.append(hex)
            }
            val bitMask = length.bitMask
            val value = bytes.last() or bitMask
            val hex = (value.toInt() and 0xFF).toString(16)
            if (bitMask > 0b0000_1111.toByte()) {
                val char = hex.first().uppercaseChar()
                stringBuilder[stringBuilder.lastIndex - 1] = char
                stringBuilder[stringBuilder.lastIndex] = '_'
            } else {
                val char = hex.last().uppercaseChar()
                stringBuilder[stringBuilder.lastIndex] = char
                stringBuilder.append('_')
            }
        }
        return stringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BitString

        if (length != other.length) return false
        if (other is ByteArrayBitStringImpl) {
            if (!bytes.contentEquals(other.bytes)) return false
        } else {
            if (!toBooleanArray().contentEquals(other.toBooleanArray())) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = length
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

fun BitString(bits: Iterable<Boolean>): BitString {
    val bitsList = bits.toList()
    val bitString = ByteArrayBitStringImpl(bitsList.size)
    bitsList.forEachIndexed { index, bit ->
        bitString[index] = bit
    }
    return bitString
}

fun BitString(vararg bits: Boolean): BitString {
    val bitString = ByteArrayBitStringImpl(bits.size)
    bits.forEachIndexed { index, bit ->
        bitString[index] = bit
    }
    return bitString
}

fun BitString(hex: String): BitString {
    val bits = hex.asSequence().map { char ->
        char.digitToInt(16).toString(2).padStart(4, '0').map { it == '1' }
    }.flatten().asIterable()
    return BitString(bits)
}

fun BitString(byteArray: ByteArray): BitString =
        ByteArrayBitStringImpl(length = byteArray.size * Byte.SIZE_BITS, bytes = byteArray)

fun BitString(length: Int): BitString = ByteArrayBitStringImpl(length)

private inline val Int.wordIndex get() = (this / Byte.SIZE_BITS) or 0
private inline val Int.bitMask get() = (1 shl (7 - (this % Byte.SIZE_BITS))).toByte()
private fun BitString.checkLength() = require(length <= BitString.MAX_LENGTH) { "BitString length expected: 0..${BitString.MAX_LENGTH}, actual: $length" }
