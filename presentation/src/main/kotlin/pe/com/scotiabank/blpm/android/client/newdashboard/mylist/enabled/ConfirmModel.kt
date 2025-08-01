package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import android.content.res.Resources
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.PaymentModelDataMapper
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.FrequentItemWrapperConfirmationEntity
import pe.com.scotiabank.blpm.android.data.entity.FrequentItemWrapperEntity
import pe.com.scotiabank.blpm.android.data.repository.PaymentDataRepository
import java.lang.ref.WeakReference

class ConfirmModel(
    dispatcherProvider: DispatcherProvider,
    private val weakResources: WeakReference<Resources?>,
    private val factory: FactoryOfRequestEntity,
    private val repository: PaymentDataRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun confirm(
        originProduct: ProductModel,
        numberOfInstallments: Int,
        operations: Collection<FrequentOperationModel>,
    ): PaymentSummaryModel = withContext(ioDispatcher) {

        val requestEntity: FrequentItemWrapperEntity = factory.createEntity(
            originProduct = originProduct,
            numberOfInstallments = numberOfInstallments,
            operations = operations,
        )

        val entityOrError: Any = repository
            .confirmMyFrequent(requestEntity)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: FrequentItemWrapperConfirmationEntity = transformToEntity(entityOrError)
        PaymentModelDataMapper.transformFrequentItemWrapperConfirmationModel(
            responseEntity,
            true,
            weakResources.get(),
        )
    }
}
