package org.ton.api.dht

import io.ktor.utils.io.core.*
import kotlinx.serialization.Serializable
import org.ton.api.adnl.AdnlNodes
import org.ton.tl.TlConstructor
import org.ton.tl.constructors.readVectorTl
import org.ton.tl.constructors.writeVectorTl

@Serializable
data class DhtNodes(
        val nodes: List<DhtNode>
) : Iterable<DhtNode> by nodes {
    fun toAdnlNodes(): AdnlNodes = AdnlNodes(nodes.map { it.toAdnlNode() })

    companion object : TlConstructor<DhtNodes>(
        type = DhtNodes::class,
        schema = "dht.nodes nodes:(vector dht.node) = dht.Nodes"
    ) {
        override fun encode(output: Output, value: DhtNodes) {
            output.writeVectorTl(value.nodes, DhtNode)
        }

        override fun decode(input: Input): DhtNodes {
            val nodes = input.readVectorTl(DhtNode)
            return DhtNodes(nodes)
        }
    }
}