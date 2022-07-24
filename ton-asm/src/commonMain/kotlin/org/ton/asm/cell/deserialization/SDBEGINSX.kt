package org.ton.asm.cell.deserialization

import org.ton.asm.Instruction
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbConstructor
import org.ton.tlb.providers.TlbConstructorProvider

object SDBEGINSX : Instruction, TlbConstructorProvider<SDBEGINSX> by SDBEGINSXTlbConstructor {
    override fun toString(): String = "SDBEGINSX"
}

private object SDBEGINSXTlbConstructor : TlbConstructor<SDBEGINSX>(
    schema = "asm_sdbeginsx#d726 = SDBEGINSX;",
    type = SDBEGINSX::class
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: SDBEGINSX) {
    }

    override fun loadTlb(cellSlice: CellSlice): SDBEGINSX = SDBEGINSX
}