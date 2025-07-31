package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import androidx.arch.core.util.Function
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.entity.products.ProductsWrapperEntity
import pe.com.scotiabank.blpm.android.data.entity.products.common.BaseProductEntity
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.products.stable.ProductRepository

class ProductPickingModel(
    dispatcherProvider: DispatcherProvider,
    private val types: String,
    private val repository: ProductRepository,
    private val mapper: Function<List<BaseProductEntity?>, ProductGroup>,
) : DispatcherProvider by dispatcherProvider {

    private val illegalResponseBody: IllegalArgumentException
        get() = IllegalArgumentException(
            "Cannot use FormatSchema of type" + Constant.SPACE_WHITE + ProductsWrapperEntity::class.java.name
        )

    @Suppress("UNUSED_PARAMETER")
    suspend fun getProductGroup(
        inputData: Map<Long, Any?>,
    ): ProductGroup = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = repository.getProductGroup(
            types = types,
            isTransactionalOnly = true,
            isMainIncluded = true,
            isAmountIncluded = false,
            isHiddenIncluded = false,
        )

        when (val responseEntity: Any? = httpResponse.body) {
            is ProductsWrapperEntity -> mapper.apply(responseEntity.products)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw illegalResponseBody
        }
    }
}
