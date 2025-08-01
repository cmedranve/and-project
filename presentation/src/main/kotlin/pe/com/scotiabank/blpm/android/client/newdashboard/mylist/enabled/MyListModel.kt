package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import android.content.res.Resources
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary.FrequentOperationSummary
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModelMapper
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.FrequentOperationSummaryEntity
import pe.com.scotiabank.blpm.android.data.entity.MyFrequentWrapperEntity
import pe.com.scotiabank.blpm.android.data.repository.NewFrequentOperationsDataRepository
import java.lang.ref.WeakReference

class MyListModel(
    dispatcherProvider: DispatcherProvider,
    private val weakResources: WeakReference<Resources?>,
    private val newFrequentOperationsDataRepository: NewFrequentOperationsDataRepository,
    private val frequentOperationTypes: Collection<FrequentOperationType>,
): DispatcherProvider by dispatcherProvider {

    suspend fun getFrequentOperationSummaries(): List<FrequentOperationSummary> = withContext(ioDispatcher) {

        val entityOrError: Any = newFrequentOperationsDataRepository.frequentOperationsSummary
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntities: List<FrequentOperationSummaryEntity> = transformToEntity(entityOrError)
        val responseEntitiesByType: Map<String, FrequentOperationSummaryEntity> = responseEntities
            .associateBy(::byTypeAsKey)
        frequentOperationTypes
            .mapNotNull { frequentType -> attemptToSummary(frequentType, responseEntitiesByType) }
    }

    private fun byTypeAsKey(entity: FrequentOperationSummaryEntity): String = entity.type.uppercase()

    private fun attemptToSummary(
        frequentOperationType: FrequentOperationType,
        responseEntitiesByType: Map<String, FrequentOperationSummaryEntity>,
    ): FrequentOperationSummary? {

        val entity: FrequentOperationSummaryEntity = responseEntitiesByType[frequentOperationType.typeFromNetworkCall]
            ?: return null

        return FrequentOperationSummary(type = frequentOperationType, total = entity.total)
    }

    suspend fun getFrequentOperationsBy(
        type: String
    ): List<FrequentOperationModel> = withContext(ioDispatcher) {

        val entityOrError: Any = newFrequentOperationsDataRepository.getFrequentOperations(type)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: List<MyFrequentWrapperEntity> = transformToEntity(entityOrError)
        FrequentOperationModelMapper.transformMyFrequentWrapperEntities(true, responseEntity, weakResources.get())
    }
}