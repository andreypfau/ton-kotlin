@file:Suppress("OPT_IN_USAGE")

package org.ton.api.pk

import kotlinx.serialization.Serializable
import org.ton.api.adnl.AdnlIdShort
import org.ton.api.pub.PublicKey
import org.ton.crypto.Decryptor
import org.ton.tl.TlCombinator

@Serializable
sealed interface PrivateKey : Decryptor {
    fun publicKey(): PublicKey
    fun toAdnlIdShort(): AdnlIdShort = publicKey().toAdnlIdShort()

    companion object : TlCombinator<PrivateKey>(
        PrivateKeyUnencrypted,
        PrivateKeyEd25519.tlConstructor(),
        PrivateKeyAes,
        PrivateKeyOverlay
    )
}