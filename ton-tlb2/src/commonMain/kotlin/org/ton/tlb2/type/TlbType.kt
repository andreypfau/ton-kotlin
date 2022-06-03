package org.ton.tlb2.type

import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice

interface TlbType {
    val sizeBits: Int

    fun load(cellSlice: CellSlice): Any
    fun store(cellBuilder: CellBuilder, value: Any)
}
