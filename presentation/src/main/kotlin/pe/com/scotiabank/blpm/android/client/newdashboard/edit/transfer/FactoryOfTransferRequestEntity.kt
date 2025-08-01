package pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer

import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.data.entity.EditAttributeFrequentPaymentEntity
import pe.com.scotiabank.blpm.android.data.entity.EditFrequentPaymentRequestEntity

class FactoryOfTransferRequestEntity(
    private val factoryOfAttributeRequest: FactoryOfAttributeRequest,
) {

    fun createEntity(
        frequentOperation: FrequentOperationModel,
    ): EditFrequentPaymentRequestEntity {

        val attributesEntities: EditAttributeFrequentPaymentEntity = factoryOfAttributeRequest.create(
            frequentOperation = frequentOperation,
        )

        return EditFrequentPaymentRequestEntity().apply {
            type = frequentOperation.type
            title = frequentOperation.title
            attributes = attributesEntities
        }
    }
}