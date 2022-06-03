package org.ton.tlb2.example

import org.ton.bitstring.BitString
import org.ton.cell.Cell
import org.ton.tlb2.TlbConstructor
import org.ton.tlb2.TlbEntity

object TickTockTlbConstructor : TlbConstructor("tick_tock") {
    val tick = bool("tick")
    val tock = bool("tock")
}

class TickTock(cell: Cell, offsetBits: Int) : TlbEntity(cell, offsetBits) {
    val tick by TickTockTlbConstructor.tick
    val tock by TickTockTlbConstructor.tock
}

fun main() {
    val cell = Cell(BitString.binary("10"))
    val tickTock = TickTock(cell, 0)
    println("tick=${tickTock.tick}")
    println("tock=${tickTock.tock}")
}
