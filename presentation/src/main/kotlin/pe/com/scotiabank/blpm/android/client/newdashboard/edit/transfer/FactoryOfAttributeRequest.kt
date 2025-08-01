package pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer

import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.data.entity.EditAttributeFrequentPaymentEntity

class FactoryOfAttributeRequest {

    fun create(
        frequentOperation: FrequentOperationModel,
    ) = EditAttributeFrequentPaymentEntity().apply {
        currency = frequentOperation.currency
        amount = frequentOperation.amount.toString()
    }
}