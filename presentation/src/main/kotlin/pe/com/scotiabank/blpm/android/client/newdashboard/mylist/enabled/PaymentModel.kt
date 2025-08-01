package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.model.DebtWrapperModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.PaymentModelDataMapper
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.DebtWrapperEntity
import pe.com.scotiabank.blpm.android.data.repository.PaymentDataRepository

class PaymentModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: PaymentDataRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun fetchDebtWrapper(
        operation: FrequentOperationModel,
    ): DebtWrapperModel = withContext(ioDispatcher) {

        val entityOrError: Any = repository
            .getDebtPayment(
                operation.institutionId.orEmpty(),
                operation.paymentCode.orEmpty(),
                operation.serviceCode.orEmpty(),
                operation.zonal.orEmpty(),
            )
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: DebtWrapperEntity = transformToEntity(entityOrError)
        PaymentModelDataMapper.transformDebtWrapperEntity(
            responseEntity,
            Constant.EMPTY_STRING,
            operation.isMaskAmount,
            operation.isAllowMultiplePayments,
            Constant.DEFAULT
        )
    }
}