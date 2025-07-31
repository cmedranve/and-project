package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.FrequentPaymentEntity
import pe.com.scotiabank.blpm.android.data.entity.PaymentAttributeEntity

class FrequentFactoryForPayment: FrequentFactory<PaymentSummaryModel> {

    override fun create(
        summary: PaymentSummaryModel,
    ): FrequentPaymentEntity = FrequentPaymentEntity().apply {

        type = Constant.PAYMENT_TYPE
        title = Constant.EMPTY_STRING
        cardNumber = summary.customerProductNumber

        val paymentAttributeEntity = PaymentAttributeEntity()
        paymentAttributeEntity.zonalId = summary.zonalId
        paymentAttributeEntity.serviceCode = summary.serviceCode
        paymentAttributeEntity.serviceDescription = summary.serviceDescription
        paymentAttributeEntity.paymentCode = summary.paymentCode
        paymentAttributeEntity.institutionId = summary.institutionId
        attributeEntities = paymentAttributeEntity
    }
}
