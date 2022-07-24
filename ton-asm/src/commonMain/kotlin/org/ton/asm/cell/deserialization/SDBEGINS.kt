package org.ton.asm.cell.deserialization

import org.ton.asm.Instruction
import org.ton.bitstring.BitString
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbConstructor
import org.ton.tlb.providers.TlbConstructorProvider

data class SDBEGINS(
    val data: BitString
) : Instruction {
    override fun toString(): String = "$data SDBEGINS"

    companion object : TlbConstructorProvider<SDBEGINS> by SDBEGINSTlbConstructor
}

private object SDBEGINSTlbConstructor : TlbConstructor<SDBEGINS>(
    schema = "asm_sdbegins#d72a_ len:uint7 data:(bits (8 * len + 3)) = SDBEGINS;",
    type = SDBEGINS::class
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: SDBEGINS) {
        val len = (value.data.size - 3) / 8
        cellBuilder.storeUInt(len, 7)
        cellBuilder.storeBits(value.data)
    }

    override fun loadTlb(cellSlice: CellSlice): SDBEGINS {
        val len = cellSlice.loadUInt(7).toInt()
        val data = cellSlice.loadBits(8 * len + 3)
        return SDBEGINS(data)
    }
}