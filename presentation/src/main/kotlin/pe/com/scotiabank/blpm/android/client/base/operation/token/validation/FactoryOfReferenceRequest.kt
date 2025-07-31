package pe.com.scotiabank.blpm.android.client.base.operation.token.validation

import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.ReferenceRequestEntity

class FactoryOfReferenceRequest {

    fun createEntity(description: String) = ReferenceRequestEntity().apply {
        smartToken = Constant.EMPTY_STRING
        reference = description
    }
}
