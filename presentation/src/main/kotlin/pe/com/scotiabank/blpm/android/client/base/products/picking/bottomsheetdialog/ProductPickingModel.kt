package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.payment.PaymentUtil
import pe.com.scotiabank.blpm.android.client.products.SelectProductDataMapper
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.domain.interactor.products.ProductPickingUseCase
import pe.com.scotiabank.blpm.android.data.entity.products.ProductsWrapperEntity
import pe.com.scotiabank.blpm.android.data.repository.products.ProductRepository

class ProductPickingModel(
    dispatcherProvider: DispatcherProvider,
    private val productRepository: ProductRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun getProducts(
        isPayableWithCreditCard: Boolean
    ): List<ProductModel> = withContext(ioDispatcher) {
        val typeId = PaymentUtil.getTypeProductsPaymentTransact(isPayableWithCreditCard)
        val params = ProductPickingUseCase.createParams(typeId)
        val entityOrError: Any = productRepository.getProducts(
                params.productTypeId,
                params.transactionalProducts,
                params.isPrincipalAccount,
                params.withoutAmounts,
                params.hiddenProducts
            )
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: ProductsWrapperEntity = transformToEntity(entityOrError)
        SelectProductDataMapper.transformProductWrapper(responseEntity)
    }
}
