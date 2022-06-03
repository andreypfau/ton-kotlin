package org.ton.tlb2.type

import org.ton.bigint.toBigInt
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice

class IntType(
    override val sizeBits: Int
) : TlbType {
    override fun load(cellSlice: CellSlice): Int {
        return cellSlice.loadInt(sizeBits).toInt()
    }

    override fun store(cellBuilder: CellBuilder, value: Any) {
        when (value) {
            is Number -> cellBuilder.storeInt(value.toBigInt(), sizeBits)
        }
    }
}
