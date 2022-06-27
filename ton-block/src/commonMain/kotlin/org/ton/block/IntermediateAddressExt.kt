package org.ton.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.cell.invoke
import org.ton.tlb.TlbConstructor

@Serializable
@SerialName("interm_addr_ext")
data class IntermediateAddressExt(
    val workchain_id: Int,
    val addr_pfx: Long
) : IntermediateAddress {
    companion object {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<IntermediateAddressExt> = IntermediateAddressExtTlbConstructor
    }
}

private object IntermediateAddressExtTlbConstructor : TlbConstructor<IntermediateAddressExt>(
    schema = "interm_addr_ext\$11 workchain_id:int32 addr_pfx:uint64 = IntermediateAddress;"
) {
    override fun storeTlb(
        cellBuilder: CellBuilder,
        value: IntermediateAddressExt
    ) = cellBuilder {
        storeInt(value.workchain_id, 32)
        storeUInt(value.addr_pfx, 64)
    }

    override fun loadTlb(
        cellSlice: CellSlice
    ): IntermediateAddressExt = cellSlice {
        val workchainId = loadInt(32).toInt()
        val addrPfx = loadUInt(64).toLong()
        IntermediateAddressExt(workchainId, addrPfx)
    }
}