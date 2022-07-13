package org.ton.api.adnl.message

import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.crypto.Base64ByteArraySerializer
import org.ton.crypto.hex
import org.ton.tl.TlConstructor
import org.ton.tl.constructors.readBytesTl
import org.ton.tl.constructors.readInt256Tl
import org.ton.tl.constructors.writeBytesTl
import org.ton.tl.constructors.writeInt256Tl

@SerialName("adnl.message.query")
@Serializable
data class AdnlMessageQuery(
    @Serializable(Base64ByteArraySerializer::class)
    val query_id: ByteArray,
    @Serializable(Base64ByteArraySerializer::class)
    val query: ByteArray
) : AdnlMessage {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AdnlMessageQuery) return false
        if (!query_id.contentEquals(other.query_id)) return false
        if (!query.contentEquals(other.query)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = query_id.contentHashCode()
        result = 31 * result + query.contentHashCode()
        return result
    }

    override fun toString() = buildString {
        append("AdnlMessageQuery(queryId=")
        append(hex(query_id))
        append(", query=")
        append(hex(query))
        append(")")
    }

    companion object : TlConstructor<AdnlMessageQuery>(
        type = AdnlMessageQuery::class,
        schema = "adnl.message.query query_id:int256 query:bytes = adnl.Message"
    ) {
        override fun encode(output: Output, value: AdnlMessageQuery) {
            output.writeInt256Tl(value.query_id)
            output.writeBytesTl(value.query)
        }

        override fun decode(input: Input): AdnlMessageQuery {
            val queryId = input.readInt256Tl()
            val query = input.readBytesTl()
            return AdnlMessageQuery(queryId, query)
        }
    }
}