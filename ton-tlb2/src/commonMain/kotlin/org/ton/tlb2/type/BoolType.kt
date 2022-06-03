package org.ton.tlb2.type

import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice

object BoolType : TlbType {
    override val sizeBits: Int = 1

    override fun load(cellSlice: CellSlice): Boolean {
        return cellSlice.loadBit()
    }

    override fun store(cellBuilder: CellBuilder, value: Any) {
        when (value) {
            is Number -> cellBuilder.storeBit(value != 0)
            is Boolean -> cellBuilder.storeBit(value)
            else -> TODO()
        }
    }
}

