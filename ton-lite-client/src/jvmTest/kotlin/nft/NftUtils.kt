package nft

import org.ton.bigint.BigInt
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.tlb.loadTlb

class NftUtils {

    data class NftFullContent(val layout: NftContentLayout, val data: ByteArray)

    enum class NftContentLayout {
        OFFCHAIN, ONCHAIN
    }
    data class NftItem (
        val init: Boolean,
        val index: VmStackValue.TinyInt,
        val collectionAddress: MsgAddress?,
        val ownerAddress: MsgAddress?,
        val contentLayout: NftContentLayout?,
        val individualContent: String
    )

    private fun decodeNftFullContent(cell: Cell): NftFullContent {
        if (cell.bits.isEmpty()) throw RuntimeException()
        var slice = cell.beginParse()
        val contentTypeTag = slice.loadUInt(8)

        val layout = if (contentTypeTag == BigInt(0))
            NftContentLayout.ONCHAIN else NftContentLayout.OFFCHAIN
        val result = byteArrayOf()

        if (contentTypeTag == BigInt(0)) {
            // process on-chain data
            throw RuntimeException("on-chain processing not implemented")
        } else {
            // process off-chain data
            result.plus(slice.bits.toByteArray())
            while (slice.refs.isNotEmpty()) {
                if (slice.refs.isNotEmpty())
                    result.plus(slice.refs[0].bits.toByteArray())
                slice = slice.refs[0].beginParse()
            }
        }

        return NftFullContent(layout, result)
    }


    fun decodeNftItem(stack: List<VmStackValue>): NftItem {
        // well I can do parsing successfully only with List<VmStackValue>
        // and need make strange casts here like :
        // (stack[2] as VmStackValue.Slice).slice.toCellSlice()

        // instead of this it will be very useful to do:

//        decodeNftItem(
//            getResult.resultStack()!!.toCell()
//        )
        // and then parse like normal
        // val init = stack.loadInt()
        // val collectionAddress = kotlin.runCatching { stack.loadTlb(MsgAddress.tlbCodec()) }.getOrNull()
        //
        // etc but this approach does not work.
        // Converted into Cell, VmStack does not meet required parsing sequence
        // stack[0].IsInt() || stack[1].IsInt() || stack[2].IsCellSlice() || stack[3].IsCellSlice() || stack[4].IsCell()

        // IDK why

        val init = stack[0] != BigInt(0)
        val index = stack[1] as VmStackValue.TinyInt
        val collectionAddress = kotlin.runCatching { (stack[2] as VmStackValue.Slice).slice.toCellSlice().loadTlb(MsgAddress.tlbCodec()) }.getOrNull()
        val ownerAddress = kotlin.runCatching { (stack[3] as VmStackValue.Slice).slice.toCellSlice().loadTlb(MsgAddress.tlbCodec()) }.getOrNull()

        var contentLayout: NftContentLayout? = null
        val content = (stack[4] as VmStackValue.Cell).cell.beginParse()
        val individualContent = if (collectionAddress != null) {
            content.loadRef().toString()
        } else { //only individual item should have TIP-64 layout
            val decodedContent = decodeNftFullContent(content.loadRef())
            contentLayout = decodedContent.layout
            String(decodedContent.data)
        }

        return NftItem(
            init = init,
            index = index,
            collectionAddress = collectionAddress,
            ownerAddress = ownerAddress,
            individualContent = individualContent,
            contentLayout = contentLayout
        )
    }
}
