package org.ton.smartcontract.wallet.v4

import kotlinx.datetime.Clock
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.bitstring.BitString
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.lite.api.LiteApi
import org.ton.smartcontract.wallet.GetPublicKeyWallet
import org.ton.smartcontract.wallet.SeqnoWallet
import org.ton.smartcontract.wallet.WalletContract

abstract class AbstractWalletV4(
    liteApi: LiteApi,
    privateKey: PrivateKeyEd25519,
    workchainId: Int = 0,
    val subwalletId: Int = DEFAULT_WALLET_ID + workchainId,
    private val timeout: Long = 60
) : WalletContract(liteApi, privateKey, workchainId), SeqnoWallet, GetPublicKeyWallet {

    override fun createDataInit(): Cell = CellBuilder.createCell {
        storeUInt(0, 32) // seqno
        storeUInt(subwalletId, 32)
        storeBytes(privateKey.publicKey().key)
        storeUInt(0, 1) // plugins dict empty
    }

    override fun createSigningMessage(seqno: Int, builder: CellBuilder.() -> Unit): Cell = CellBuilder.createCell {
        storeUInt(subwalletId, 32)
        if (seqno == 0) {
            storeBits(BitString("FFFFFFFF"))
        } else {
            val now = Clock.System.now().toEpochMilliseconds() / 1000
            storeUInt(now + timeout, 32)
        }
        storeUInt(seqno, 32)
        storeUInt(0, 8) // op
        apply(builder)
    }

    companion object {
        const val DEFAULT_WALLET_ID = 698983191
    }
}
