package org.ton.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbConstructor

@Serializable
@SerialName("account_uninit")
object AccountUninit : AccountState {
    fun tlbCodec(): TlbConstructor<AccountUninit> = AccountUninitTlbConstructor

    override val status: AccountStatus = AccountStatus.UNINIT

    override fun toString(): String = "account_uninit"
}

private object AccountUninitTlbConstructor : TlbConstructor<AccountUninit>(
    schema = "account_uninit\$00 = AccountState;"
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: AccountUninit) {
    }

    override fun loadTlb(cellSlice: CellSlice): AccountUninit {
        return AccountUninit
    }
}