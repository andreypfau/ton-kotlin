package org.ton.tlb2

import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb2.exception.DuplicateColumnException
import org.ton.tlb2.type.BoolType
import org.ton.tlb2.type.TlbType

open class TlbConstructor(name: String = "") : TlbType {
    open val tableName: String = when {
        name.isNotEmpty() -> name
        else -> this::class.simpleName.toString()
    }

    private val _columns = mutableListOf<Column<*>>()

    override val sizeBits: Int
        get() = _columns.sumOf { it.type.sizeBits }

    fun bool(name: String): Column<Boolean> = registerColumn(name, BoolType)

    fun <T> registerColumn(name: String, type: TlbType): Column<T> = Column<T>(this, name, type).also {
        _columns.addColumn(it)
    }

    private fun MutableList<Column<*>>.addColumn(column: Column<*>) {
        if (this.any { it.name == column.name }) {
            throw DuplicateColumnException(column.name, tableName)
        }
        this.add(column)
    }

    override fun load(cellSlice: CellSlice): Any {
        return _columns.associate {
            it.name to it.type.load(cellSlice)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun store(cellBuilder: CellBuilder, value: Any) {
        if (value is Map<*, *>) {
            value as Map<String, Any>
            _columns.forEach {
                val fieldValue = requireNotNull(value[it.name])
                it.type.store(cellBuilder, fieldValue)
            }
        } else {
            TODO()
        }
    }
}
