package org.ton.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.cell.*
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Serializable
@SerialName("msg_import_imm")
data class MsgImportImm(
    val in_msg: MsgEnvelope,
    val transaction: Transaction,
    val fwd_fee: Coins
) : InMsg {
    companion object {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<MsgImportImm> = MsgImportImmTlbConstructor
    }
}

private object MsgImportImmTlbConstructor : TlbConstructor<MsgImportImm>(
    schema = "msg_import_imm\$011 in_msg:^MsgEnvelope transaction:^Transaction fwd_fee:Coins = InMsg;"
) {
    val coins by lazy { Coins.tlbCodec() }

    override fun storeTlb(
        cellBuilder: CellBuilder,
        value: MsgImportImm
    ) = cellBuilder {
        storeRef {
            storeTlb(MsgEnvelope, value.in_msg)
        }
        storeRef {
            storeTlb(Transaction, value.transaction)
        }
        storeTlb(coins, value.fwd_fee)
    }

    override fun loadTlb(
        cellSlice: CellSlice
    ): MsgImportImm = cellSlice {
        val inMsg = loadRef {
            loadTlb(MsgEnvelope)
        }
        val transaction = loadRef {
            loadTlb(Transaction)
        }
        val fwdFee = loadTlb(coins)
        MsgImportImm(inMsg, transaction, fwdFee)
    }
}
