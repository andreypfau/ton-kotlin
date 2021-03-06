@file:Suppress("OPT_IN_USAGE")

package org.ton.block

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor

@JsonClassDiscriminator("@type")
@Serializable
sealed interface InMsg {
    companion object : TlbCodec<InMsg> by InMsgTlbCombinator {
        @JvmStatic
        fun tlbCodec(): TlbCombinator<InMsg> = InMsgTlbCombinator
    }
}

private object InMsgTlbCombinator : TlbCombinator<InMsg>() {
    val ext = MsgImportExt.tlbCodec()
    val ihr = MsgImportIhr.tlbCodec()
    val imm = MsgImportImm.tlbCodec()
    val fin = MsgImportFin.tlbCodec()
    val tr = MsgImportTr.tlbCodec()
    val discardFin = MsgDiscardFin.tlbCodec()
    val discardTr = MsgDiscardTr.tlbCodec()

    override val constructors: List<TlbConstructor<out InMsg>> =
        listOf(
            ext, ihr, imm, fin, tr, discardFin, discardTr
        )

    override fun getConstructor(
        value: InMsg
    ): TlbConstructor<out InMsg> = when (value) {
        is MsgDiscardFin -> discardFin
        is MsgDiscardTr -> discardTr
        is MsgImportExt -> ext
        is MsgImportFin -> fin
        is MsgImportIhr -> ihr
        is MsgImportImm -> imm
        is MsgImportTr -> tr
    }
}