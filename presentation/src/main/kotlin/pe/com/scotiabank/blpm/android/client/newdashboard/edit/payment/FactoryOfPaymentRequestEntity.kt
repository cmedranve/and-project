package pe.com.scotiabank.blpm.android.client.newdashboard.edit.payment

import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.data.entity.EditFrequentPaymentRequestEntity

class FactoryOfPaymentRequestEntity {

    fun createEntity(
        frequentOperation: FrequentOperationModel,
    ) = EditFrequentPaymentRequestEntity().apply {
        type = frequentOperation.type
        title = frequentOperation.title
    }
}