package pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.EditFrequentPaymentRequestEntity
import pe.com.scotiabank.blpm.android.data.repository.FrequentOperationDataRepository

class EditTransferModel(
    dispatcherProvider: DispatcherProvider,
    private val factoryOfRequestEntity: FactoryOfTransferRequestEntity,
    private val repository: FrequentOperationDataRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun edit(
        frequentOperation: FrequentOperationModel,
    ): Any = withContext(ioDispatcher) {

        val requestEntity: EditFrequentPaymentRequestEntity = factoryOfRequestEntity.createEntity(
            frequentOperation = frequentOperation,
        )

        val entityOrError: Any = repository
            .editTransaction(frequentOperation.id, requestEntity)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Any = transformToEntity(entityOrError)
        responseEntity
    }
}