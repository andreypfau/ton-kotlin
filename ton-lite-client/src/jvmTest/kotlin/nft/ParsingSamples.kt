package nft

import org.ton.block.AddrStd
import org.ton.block.VmStack
import org.ton.block.VmStackList
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.crypto.base64
import org.ton.crypto.hex
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.api.liteserver.LiteServerRunMethodResult
import org.ton.lite.client.LiteClient
import org.ton.logger.Logger
import org.ton.logger.PrintLnLogger
import org.ton.tlb.storeTlb

val mainNetNft = "EQB8zFOBBsgiERJ_MG7k_OPmw101t0ViOyuEry4AtQTLDqM6"
val mainNetNftCollection = "EQAo92DYMokxghKcq-CkCGSk_MgXY5Fo1SPW20gkvZl75iCN"


suspend fun main() {

    val getResult = runGetMethod(mainNetNft, "get_nft_data")

    getResult.resultStack()?.let { stack ->
        if (stack.depth != 5) {
            throw RuntimeException("Invalid stack data")
        }
        println(
            NftUtils().decodeNftItem(getResult.resultValues()!!)
        )
    }
}

fun VmStack.toCell(): Cell {
    val cellBuilder = CellBuilder.beginCell()
    cellBuilder.storeTlb(VmStack.tlbCodec(), this)
    return cellBuilder.endCell()
}

fun getLiteClient(): LiteClient {
    return LiteClient(
        ipv4 = 908566172,
        port = 51565,
        publicKey = base64("TDg+ILLlRugRB4Kpg3wXjPcoc+d+Eeb7kuVe16CS9z8="),
        logger = PrintLnLogger("TON LiteClient", Logger.Level.DEBUG)
    )
}


suspend fun runGetMethod(address: String, method: String): LiteServerRunMethodResult {

    val liteClient = getLiteClient().connect()
    val masterchainInfo = liteClient.getMasterchainInfo()
    val lastBlock = masterchainInfo.last

    return liteClient.runSmcMethod(
        31,
        lastBlock,
        LiteServerAccountId(AddrStd(address)),
        method
    )
}
