package org.ton.tlb2

import org.ton.tlb2.type.TlbType

class Column<T>(
    val tlbConstructor: TlbConstructor,
    val name: String,
    val type: TlbType
)
