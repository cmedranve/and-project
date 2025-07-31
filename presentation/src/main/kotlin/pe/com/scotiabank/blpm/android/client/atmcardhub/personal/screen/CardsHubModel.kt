package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.Context
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.debitcard.DebitCard
import pe.com.scotiabank.blpm.android.client.model.AbstractMovementWrapperModel
import pe.com.scotiabank.blpm.android.client.model.BaseProductDetailModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.products.NewProductDataMapper
import pe.com.scotiabank.blpm.android.client.products.detailproducts.ProductDetailModelDataMapper
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.BaseProductDetailEntity
import pe.com.scotiabank.blpm.android.data.entity.GateWrapperEntity
import pe.com.scotiabank.blpm.android.data.entity.MovementWrapperEntity
import pe.com.scotiabank.blpm.android.data.entity.creditcard.CreditCardRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardsResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.NewCredentialsEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.entity.products.ProductsWrapperEntity
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.NewGatesDataRepository
import pe.com.scotiabank.blpm.android.data.repository.creditcard.CreditCardRepository
import pe.com.scotiabank.blpm.android.data.repository.debitcard.DebitCardRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.ProductRepository
import pe.com.scotiabank.blpm.android.data.util.Constant
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

class CardsHubModel(
    dispatcherProvider: DispatcherProvider,
    val pushOtpFlowChecker: PushOtpFlowChecker,
    private val appModel: AppModel,
    private val weakAppContext: WeakReference<Context?>,
    private val gatesDataRepository: NewGatesDataRepository,
    private val productRepository: ProductRepository,
    private val debitCardRepository: DebitCardRepository,
    private val creditCardRepository: CreditCardRepository,
    private val mapper: CardsHubMapper,
) : DispatcherProvider by dispatcherProvider {

    var currentCard: Any? = null

    var operationId: String = Constant.STRING_EMPTY
        private set

    private fun createExceptionOnIllegalResponseBody(kClass: KClass<*>): IllegalArgumentException {
        return IllegalArgumentException( "Cannot use FormatSchema of type " + kClass.java.name )
    }

    suspend fun getCreditCardList(
        offline: Boolean,
        productType: String,
        transactional: Boolean,
        hiddenProducts: Boolean
    ): List<NewProductModel> = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = productRepository.getProductGroup(
            types = productType,
            isTransactionalOnly = transactional,
            isMainIncluded = false,
            isAmountIncluded = offline,
            isHiddenIncluded = hiddenProducts,
        )

        when (val responseEntity: Any? = httpResponse.body) {
            is ProductsWrapperEntity -> {
                val products: List<NewProductModel> = NewProductDataMapper.transformProductHubWrapper(
                    responseEntity,
                    weakAppContext.get(),
                )
                NewProductDataMapper.getCreditCards(products)
            }

            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(ProductsWrapperEntity::class)
        }
    }

    suspend fun getProductDetailV2(
        productId: Long,
    ): BaseProductDetailModel = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = productRepository.getProductDetail(productId)

        when (val responseEntity: Any? = httpResponse.body) {
            is BaseProductDetailEntity -> {
                val isNaturalPerson = DashboardType.PERSONAL === appModel.dashboardType
                ProductDetailModelDataMapper.transformProductDetail(
                    responseEntity,
                    weakAppContext.get()?.resources,
                    isNaturalPerson
                )
            }

            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(BaseProductDetailEntity::class)
        }
    }

    suspend fun getMovements(
        id: Long,
        page: Int
    ): AbstractMovementWrapperModel = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = productRepository.getMovements(
            id = id,
            page = page,
        )

        when (val responseEntity: Any? = httpResponse.body) {
            is MovementWrapperEntity -> ProductDetailModelDataMapper.transformMovementWrapper(responseEntity)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(MovementWrapperEntity::class)
        }
    }

    suspend fun getGatesByProductType(
        productId: Long,
    ): GateWrapperEntity = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = gatesDataRepository.getGatesByProduct(productId, GATES_PRODUCT_CONTEXT)

        when (val responseEntity: Any? = httpResponse.body) {
            is GateWrapperEntity -> responseEntity
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(GateWrapperEntity::class)
        }
    }

    suspend fun getDebitCardHub(): DebitCardHub = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = debitCardRepository.getDebitCardHub()

        when (val responseEntity: Any? = httpResponse.body) {
            is CardsResponseEntity -> mapper.toDebitCardHub(responseEntity)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(CardsResponseEntity::class)
        }
    }

    suspend fun fetchOperationIdCreditCard(product: NewProductModel) = withContext(ioDispatcher) {

        val requestEntity = CreditCardRequestEntity(product.id.toString())
        val acceptHeader = pickAcceptHeaderForCreditCardTransactionId()

        val httpResponse: HttpResponse<*> = creditCardRepository.getTransactionId(acceptHeader, requestEntity)

        when (val responseEntity: Any? = httpResponse.body) {
            is NewCredentialsEntity -> updateOperationId(responseEntity, product)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }

    private fun pickAcceptHeaderForCreditCardTransactionId(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> Constant.ACCEPT_CREDIT_CARD_CREDENTIALS_V2_HEADER
        else -> String.EMPTY
    }

    suspend fun sendOperationIdCreditCard() = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = creditCardRepository.sendOperationId(operationId)

        when (val responseEntity: Any? = httpResponse.body) {
            is Unit -> Unit
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }

    suspend fun fetchOperationIdDebitCard(debitCard: DebitCard) = withContext(ioDispatcher) {

        val requestEntity = CardRequestEntity(debitCard.cardId)
        val acceptHeader = pickAcceptHeaderForDebitCardTransactionId()

        val httpResponse: HttpResponse<*> = debitCardRepository.getTransactionId(acceptHeader, requestEntity)

        when (val responseEntity: Any? = httpResponse.body) {
            is NewCredentialsEntity -> updateOperationId(responseEntity, debitCard)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(NewCredentialsEntity::class)
        }
    }

    private fun pickAcceptHeaderForDebitCardTransactionId(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> Constant.ACCEPT_DEBIT_CARD_CREDENTIALS_V2_HEADER
        else -> String.EMPTY
    }

    private fun updateOperationId(responseEntity: NewCredentialsEntity, currentCard: Any) {
        this.operationId = responseEntity.transactionId.orEmpty()
        this.currentCard = currentCard
    }

    suspend fun sendOperationIdDebitCard() = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = debitCardRepository.sendOperationId(operationId)

        when (val responseEntity: Any? = httpResponse.body) {
            is Unit -> Unit
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(Unit::class)
        }
    }

    fun createAtmCardInfo(
        authId: String,
        authTracking: String,
        cardSettingFlags: CardSettingFlags,
    ): AtmCardInfo? = when (val card: Any? = currentCard) {
        is DebitCard -> {
            debitCardToCardInfo(card = card, authId = authId, authTracking = authTracking, cardSettingFlags = cardSettingFlags)
        }
        is NewProductModel -> {
            productToCardInfo(product = card, authId = authId, authTracking = authTracking, cardSettingFlags = cardSettingFlags)
        }
        else -> null
    }

    private fun debitCardToCardInfo(
        card: DebitCard,
        authId: String,
        authTracking: String,
        cardSettingFlags: CardSettingFlags,
    ): AtmCardInfo = mapper.toAtmCardInfo(
        debitCard = card,
        authId = authId,
        authTracking = authTracking,
        operationId = operationId,
        cardSettingFlags = cardSettingFlags,
    )

    private fun productToCardInfo(
        product: NewProductModel,
        authId: String,
        authTracking: String,
        cardSettingFlags: CardSettingFlags,
    ): AtmCardInfo = mapper.toAtmCardInfo(
        product = product,
        authId = authId,
        authTracking = authTracking,
        operationId = operationId,
        cardSettingFlags = cardSettingFlags,
    )

    companion object {
        val GATES_PRODUCT_CONTEXT = arrayOf("PRODUCT_DETAILS", "PRODUCT_DETAILS_OPERATIONS")
    }
}
