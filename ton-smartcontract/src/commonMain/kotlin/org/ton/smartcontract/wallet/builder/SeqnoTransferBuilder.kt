package org.ton.smartcontract.wallet.builder

import org.ton.cell.Cell
import org.ton.cell.CellBuilder

interface SeqnoTransferBuilder : TransferBuilder {
    var seqno: Int

    override fun createData(builder: CellBuilder.() -> Unit): Cell = super.createData {
        storeUInt(seqno, 32)
        apply(builder)
    }
}
