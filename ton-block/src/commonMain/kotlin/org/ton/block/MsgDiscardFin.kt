package org.ton.block

import org.ton.cell.*
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class MsgDiscardFin(
    val in_msg: MsgEnvelope,
    val transaction_id: Long,
    val fwd_fee: Coins
) : InMsg {
    companion object {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<MsgDiscardFin> = MsgDiscardFinTlbConstructor
    }
}

private object MsgDiscardFinTlbConstructor : TlbConstructor<MsgDiscardFin>(
    schema = "msg_discard_fin\$110 in_msg:^MsgEnvelope transaction_id:uint64 fwd_fee:Coins = InMsg;"
) {
    override fun storeTlb(
        cellBuilder: CellBuilder,
        value: MsgDiscardFin
    ) = cellBuilder {
        storeRef {
            storeTlb(MsgEnvelope, value.in_msg)
        }
        storeUInt(value.transaction_id, 64)
        storeTlb(Coins, value.fwd_fee)
    }

    override fun loadTlb(
        cellSlice: CellSlice
    ): MsgDiscardFin = cellSlice {
        val inMsg = loadRef {
            loadTlb(MsgEnvelope)
        }
        val transactionId = loadUInt(64).toLong()
        val fwdFee = loadTlb(Coins)
        MsgDiscardFin(inMsg, transactionId, fwdFee)
    }
}