package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import pe.com.scotiabank.blpm.android.client.model.TransferSummaryModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.FrequentPaymentEntity
import pe.com.scotiabank.blpm.android.data.entity.TransferAttributeEntity
import java.math.BigDecimal

class FrequentFactoryForTransferOtherBank: FrequentFactory<TransferSummaryModel> {

    override fun create(
        summary: TransferSummaryModel,
    ): FrequentPaymentEntity = FrequentPaymentEntity().apply {

        type = Constant.TRANSFER_TYPE
        title = summary.reference
        cardNumber = summary.customerProductNumber

        val transferAttributeEntity = TransferAttributeEntity()
        transferAttributeEntity.amount = BigDecimal(summary.amount)
        transferAttributeEntity.creditAccount = summary.creditAccount
        transferAttributeEntity.currency = summary.currency
        transferAttributeEntity.isOwner = summary.isOwner
        transferAttributeEntity.recipientName = summary.recipientName
        transferAttributeEntity.transferType = summary.transferType
        attributeEntities = transferAttributeEntity
    }
}
