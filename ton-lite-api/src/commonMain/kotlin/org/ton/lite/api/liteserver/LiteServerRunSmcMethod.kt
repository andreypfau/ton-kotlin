package org.ton.lite.api.liteserver

import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.cell.BagOfCells
import org.ton.crypto.Base64ByteArraySerializer
import org.ton.crypto.crc16.Crc16
import org.ton.tl.TlConstructor

@Serializable
data class LiteServerRunSmcMethod(
    val mode: Int,
    val id: TonNodeBlockIdExt,
    val account: LiteServerAccountId,
    @SerialName("method_id")
    val methodId: Long,
    @Serializable(Base64ByteArraySerializer::class)
    val params: ByteArray
) {
    constructor(
        mode: Int,
        id: TonNodeBlockIdExt,
        account: LiteServerAccountId,
        methodName: String,
        params: BagOfCells
    ) : this(mode, id, account, methodId(methodName), params.toByteArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LiteServerRunSmcMethod

        if (mode != other.mode) return false
        if (id != other.id) return false
        if (account != other.account) return false
        if (methodId != other.methodId) return false
        if (!params.contentEquals(other.params)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mode
        result = 31 * result + id.hashCode()
        result = 31 * result + account.hashCode()
        result = 31 * result + methodId.hashCode()
        result = 31 * result + params.contentHashCode()
        return result
    }

    companion object : TlConstructor<LiteServerRunSmcMethod>(
        type = LiteServerRunSmcMethod::class,
        schema = "liteServer.runSmcMethod mode:# id:tonNode.blockIdExt account:liteServer.accountId method_id:long params:bytes = liteServer.RunMethodResult"
    ) {
        fun methodId(methodName: String): Long = Crc16.CCITT_FALSE(methodName.encodeToByteArray()).toLong() or 0x10000

        override fun encode(output: Output, message: LiteServerRunSmcMethod) {
            output.writeIntLittleEndian(message.mode)
            output.writeTl(message.id, TonNodeBlockIdExt)
            output.writeTl(message.account, LiteServerAccountId)
            output.writeLongLittleEndian(message.methodId)
            output.writeByteArray(message.params)
        }

        override fun decode(input: Input): LiteServerRunSmcMethod {
            val mode = input.readIntLittleEndian()
            val id = input.readTl(TonNodeBlockIdExt)
            val account = input.readTl(LiteServerAccountId)
            val methodId = input.readLongLittleEndian()
            val params = input.readByteArray()
            return LiteServerRunSmcMethod(mode, id, account, methodId, params)
        }
    }
}