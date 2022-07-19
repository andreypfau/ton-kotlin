package org.ton.smartcontract.wallet.v1

import org.ton.api.pk.PrivateKeyEd25519
import org.ton.crypto.base64
import org.ton.crypto.hex
import org.ton.lite.client.LiteClient
import org.ton.logger.Logger
import org.ton.logger.PrintLnLogger

private val privateKey = PrivateKeyEd25519(ByteArray(32))

private fun liteClient() = LiteClient(
    ipv4 = 1426768764,
    port = 13724,
    publicKey = base64("R1KsqYlNks2Zows+I9s4ywhilbSevs9dH1x2KF9MeSU="),
    logger = PrintLnLogger("TON SimpleWalletR3", Logger.Level.DEBUG)
)

suspend fun main() {
    val liteClient = liteClient()
    val wallet = TransferWalletV1R3(privateKey)
    val address = wallet.address()
    println("Source wallet address = ${address.toString(userFriendly = false)}")
    println("Non-bounceable address (for init only): ${address.toString(bounceable = false, testOnly = true)}")
    println("Bounceable address (for later access): ${address.toString(bounceable = true, testOnly = true)}")
    println("Corresponding public key is ${hex(wallet.publicKey().key).uppercase()}")

    liteClient.connect()
    val block = liteClient.getMasterchainInfo().last

    println("seqno: ${wallet.seqno(liteClient, block)}")
    println("get_public_key: ${wallet.getPublicKey(liteClient, block)}")
}
