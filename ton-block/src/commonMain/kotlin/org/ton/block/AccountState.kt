@file:Suppress("OPT_IN_USAGE")

package org.ton.block

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor

@Serializable
@JsonClassDiscriminator("@type")
sealed interface AccountState {
    companion object : TlbCodec<AccountState> by AccountStateTlbCombinator {
        @JvmStatic
        fun tlbCodec(): TlbCombinator<AccountState> = AccountStateTlbCombinator
    }

    val status: AccountStatus
}

private object AccountStateTlbCombinator : TlbCombinator<AccountState>() {
    val uninit = AccountUninit.tlbCodec()
    val active = AccountActive.tlbCodec()
    val frozen = AccountFrozen.tlbCodec()

    override val constructors: List<TlbConstructor<out AccountState>> =
        listOf(uninit, active, frozen)

    override fun getConstructor(
        value: AccountState
    ): TlbConstructor<out AccountState> = when (value) {
        is AccountUninit -> uninit
        is AccountActive -> active
        is AccountFrozen -> frozen
    }
}