package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.repository.FrequentOperationDataRepository

class DeleteOperationModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: FrequentOperationDataRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun deleteTransaction(
        frequentOperation: FrequentOperationModel,
    ): Any = withContext(ioDispatcher) {

        val entityOrError: Any = repository
            .deleteTransaction(frequentOperation.id)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Any = transformToEntity(entityOrError)
        responseEntity
    }
}