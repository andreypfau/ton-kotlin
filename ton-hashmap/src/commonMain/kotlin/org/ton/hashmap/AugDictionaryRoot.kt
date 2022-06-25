package org.ton.hashmap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@SerialName("ahme_root")
@Serializable
data class AugDictionaryRoot<X : Any, Y : Any>(
    val root: AugDictionaryEdge<X, Y>,
    override val extra: Y
) : AugDictionary<X, Y> {
    override fun toString(): String = "ahme_root(root:$root extra:$extra)"

    companion object {
        @JvmStatic
        fun <X : Any, Y : Any> tlbCodec(
            n: Int,
            x: TlbCodec<X>,
            y: TlbCodec<Y>
        ): TlbConstructor<AugDictionaryRoot<X, Y>> = AugDictionaryRootTlbConstructor(n, x, y)
    }
}

private class AugDictionaryRootTlbConstructor<X : Any, Y : Any>(
    val n: Int,
    val x: TlbCodec<X>,
    val y: TlbCodec<Y>
) : TlbConstructor<AugDictionaryRoot<X, Y>>(
    schema = "ahme_root\$1 {n:#} {X:Type} {Y:Type} root:^(AugDictionaryEdge n X Y) extra:Y = AugDictionary n X Y;"
) {
    val edge by lazy {
        AugDictionaryEdge.tlbCodec(n, x, y)
    }

    override fun storeTlb(
        cellBuilder: CellBuilder,
        value: AugDictionaryRoot<X, Y>
    ) = cellBuilder {
        storeTlb(edge, value.root)
        storeTlb(y, value.extra)
    }

    override fun loadTlb(
        cellSlice: CellSlice
    ): AugDictionaryRoot<X, Y> = cellSlice {
        val root = loadTlb(edge)
        val extra = loadTlb(y)
        AugDictionaryRoot(root, extra)
    }
}