package org.ton.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.cell.invoke
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Serializable
@SerialName("account_active")
data class AccountActive(
    @SerialName("_")
    val init: StateInit
) : AccountState {
    override fun toString(): String = "account_active($init)"

    override val status: AccountStatus = AccountStatus.ACTIVE

    companion object {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<AccountActive> = AccountActiveTlbConstructor
    }
}

private object AccountActiveTlbConstructor : TlbConstructor<AccountActive>(
    schema = "account_active\$1 _:StateInit = AccountState;"
) {
    override fun storeTlb(
        cellBuilder: CellBuilder,
        value: AccountActive
    ) = cellBuilder {
        storeTlb(StateInit, value.init)
    }

    override fun loadTlb(
        cellSlice: CellSlice
    ): AccountActive = cellSlice {
        val init = loadTlb(StateInit)
        AccountActive(init)
    }
}