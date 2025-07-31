package pe.com.scotiabank.blpm.android.client.base.operation.token.validation

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.ReferenceRequestEntity

class ConfirmModel<R: Any>(
    dispatcherProvider: DispatcherProvider,
    private val factoryOfReferenceRequest: FactoryOfReferenceRequest,
    private val confirmRepository: ConfirmRepository<R>,
): DispatcherProvider by dispatcherProvider {

    suspend fun confirm(
        transactionId: String,
        authTracking: String,
        authId: String,
        description: String
    ): R = withContext(ioDispatcher) {

        val requestEntity: ReferenceRequestEntity = factoryOfReferenceRequest.createEntity(description)
        val entityOrError: Any = confirmRepository
            .confirmOperation(
                authTracking = authTracking,
                authToken = authId,
                transactionId = transactionId,
                referenceRequestEntity = requestEntity,
            )
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: R = transformToEntity(entityOrError)
        responseEntity
    }
}
