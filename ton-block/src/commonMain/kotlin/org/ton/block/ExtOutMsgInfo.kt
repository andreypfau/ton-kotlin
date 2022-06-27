package org.ton.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.cell.invoke
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@SerialName("ext_out_msg_info")
@Serializable
data class ExtOutMsgInfo(
    val src: MsgAddressInt,
    val dest: MsgAddressExt,
    @SerialName("created_lt")
    val createdLt: Long,
    @SerialName("created_at")
    val createdAt: Int
) : CommonMsgInfo {
    companion object {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<ExtOutMsgInfo> = ExtOutMsgInfoTlbConstructor()
    }
}

private class ExtOutMsgInfoTlbConstructor : TlbConstructor<ExtOutMsgInfo>(
    schema = "ext_out_msg_info\$11 src:MsgAddressInt dest:MsgAddressExt created_lt:uint64 created_at:uint32 = CommonMsgInfo;"
) {
    private val msgAddressIntCodec by lazy {
        MsgAddressInt.tlbCodec()
    }
    private val msgAddressExtCodec by lazy {
        MsgAddressExt.tlbCodec()
    }

    override fun storeTlb(
        cellBuilder: CellBuilder, value: ExtOutMsgInfo
    ) = cellBuilder {
        storeTlb(msgAddressIntCodec, value.src)
        storeTlb(msgAddressExtCodec, value.dest)
        storeUInt(value.createdLt, 64)
        storeUInt(value.createdAt, 32)
    }

    override fun loadTlb(
        cellSlice: CellSlice
    ): ExtOutMsgInfo = cellSlice {
        val src = loadTlb(msgAddressIntCodec)
        val dest = loadTlb(msgAddressExtCodec)
        val createdLt = loadUInt(64).toLong()
        val createdAt = loadUInt(32).toInt()
        ExtOutMsgInfo(src, dest, createdLt, createdAt)
    }
}