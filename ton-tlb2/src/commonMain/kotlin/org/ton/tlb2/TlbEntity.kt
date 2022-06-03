package org.ton.tlb2

import org.ton.cell.Cell
import kotlin.reflect.KProperty

open class TlbEntity(
    val cell: Cell,
    val offsetBits: Int
) {
    @Suppress("UNCHECKED_CAST")
    operator fun <T> Column<T>.getValue(o: TlbEntity, desc: KProperty<*>): T {
        val cellSlice = cell.beginParse()
        cellSlice.loadBits(offsetBits)
        val fieldMap = tlbConstructor.load(cellSlice) as Map<String, Any>
        return fieldMap[name] as T
    }
}
