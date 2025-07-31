package pe.com.scotiabank.blpm.android.client.base.operation.completion

import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.transfer.ReferenceRequestEntity

class FactoryOfReferenceRequest {

    fun createEntity(description: String) = ReferenceRequestEntity(
        smartToken = String.EMPTY,
        reference = description,
    )
}
