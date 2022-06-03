package org.ton.smartcontract.wallet

import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.Coins
import org.ton.block.MsgAddressInt
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
    liteClient.connect()
    val wallet = WalletV2R1(liteClient, privateKey)
    val address = wallet.address()
    println("Source wallet address = ${address.toString(userFriendly = false)}")
    println("Non-bounceable address (for init only): ${address.toString(bounceable = false, testOnly = true)}")
    println("Bounceable address (for later access): ${address.toString(bounceable = true, testOnly = true)}")
    println("Corresponding public key is ${hex(wallet.publicKey.key).uppercase()}")
    wallet.transfer(
        MsgAddressInt.parse("kf8zMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzM_BP"),
        false,
        Coins.of(0.01),
        1,
        "Hello TON \uD83D\uDE80"
    )
}
