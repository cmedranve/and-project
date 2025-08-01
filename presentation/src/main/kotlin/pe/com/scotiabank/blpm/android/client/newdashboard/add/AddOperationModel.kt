package pe.com.scotiabank.blpm.android.client.newdashboard.add

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.mapper.RecentTransactionsMapper
import pe.com.scotiabank.blpm.android.data.model.RecentTransactionModel
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.response.RecentTransactionsResponse
import pe.com.scotiabank.blpm.android.data.repository.RecentTransactionsRepository
import pe.com.scotiabank.blpm.android.data.repository.mylist.MyListRepository
import kotlin.reflect.KClass

class AddOperationModel(
    dispatcherProvider: DispatcherProvider,
    private val myListRepository: MyListRepository,
    private val repository: RecentTransactionsRepository,
    private val mapper: RecentTransactionsMapper,
): DispatcherProvider by dispatcherProvider {

    private fun createExceptionOnIllegalResponseBody(kClass: KClass<*>) = IllegalArgumentException(
        "Cannot use FormatSchema of type" + Constant.SPACE_WHITE + kClass.java.name
    )

    suspend fun getRecentOperationsBy(
        type: String
    ): List<RecentTransactionModel> = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = myListRepository.getListRecentPayment(type)

        when (val responseEntity: Any? = httpResponse.body) {
            is Array<*> -> mapper.toRecentTransactions(responseEntity)
            is Unit -> emptyList()
            else -> throw createExceptionOnIllegalResponseBody(RecentTransactionsResponse::class)
        }
    }

    suspend fun saveRecentOperations(
        recentTransactions: List<RecentTransactionModel>,
    ): Any = withContext(ioDispatcher) {

        val entityOrError: Any = repository
            .saveListRecentTransactions(recentTransactions)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Any = transformToEntity(entityOrError)
        responseEntity
    }
}