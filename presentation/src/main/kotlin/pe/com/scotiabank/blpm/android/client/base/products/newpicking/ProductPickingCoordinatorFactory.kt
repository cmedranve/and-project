package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import androidx.core.util.Consumer
import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.number.IntegerParser
import pe.com.scotiabank.blpm.android.client.base.products.ProductMapper
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.payment.PaymentUtil
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.RestBusinessProductApiService
import pe.com.scotiabank.blpm.android.data.net.RestPersonalProductApiService
import pe.com.scotiabank.blpm.android.data.repository.products.stable.BusinessProductRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.PersonalProductRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.ProductRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class ProductPickingCoordinatorFactory(
    private val hub: Hub,
    private val analyticConsumer: Consumer<AnalyticEventData<*>>,
    private val embeddedDataName: String,
    private val retrofit: Retrofit,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    private val appModel: AppModel
        get() = hub.appModel

    fun create(carrier: CarrierFromPickingConsumer): ProductPickingCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        val factoryOfMainTopComposite = createFactoryOfMainTopComposite(
            uiStateHolder = uiStateHolder,
            idRegistry = idRegistry,
            carrier = carrier,
        )

        return ProductPickingCoordinator(
            titleText = carrier.titleText,
            factoryOfAppBarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = factoryOfMainTopComposite,
            factoryOfAnchoredBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            weakResources = hub.weakResources,
            productGroup = carrier.productGroup,
            carrierFromPickingConsumer = carrier,
            integerParser = IntegerParser(numberFormat = hub.integerNumberFormat),
            idRegistry = idRegistry,
            availabilityRegistry = createAvailabilityRegistry(idRegistry),
            analyticConsumer = analyticConsumer,
            embeddedDataName = embeddedDataName,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
    )

    private fun createFactoryOfMainTopComposite(
        uiStateHolder: UiStateHolder,
        idRegistry: IdRegistry,
        carrier: CarrierFromPickingConsumer,
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        uiStateHolder = uiStateHolder,
        idRegistry = idRegistry,
        appModel = appModel,
        weakResources = hub.weakResources,
        amountFormatter = hub.currencyFormatter,
        exchangeRateFormatter = hub.exchangeRateFormatter,
        formatterOfProductName = hub.formatterOfProductName,
        factoryOfOneColumnTextEntity = hub.factoryOfOneColumnTextEntity,
        carrier = carrier,
    )

    fun createProductPickingModel(isPayableWithCreditCard: Boolean): ProductPickingModel {

        val types: String = PaymentUtil.getTypeProductsPaymentTransact(isPayableWithCreditCard)
        val repository: ProductRepository = createProductRepository()
        val productMapper = ProductMapper()
        val mapper = Mapper(productMapper)

        return ProductPickingModel(hub.dispatcherProvider, types, repository, mapper)
    }

    private fun createProductRepository(): ProductRepository {

        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return createBusinessProductRepository()
        }

        return createPersonalProductRepository()
    }

    private fun createBusinessProductRepository(): BusinessProductRepository {
        val api: RestBusinessProductApiService = retrofit.create(
            RestBusinessProductApiService::class.java,
        )
        return BusinessProductRepository(api, hub.objectMapper)
    }

    private fun createPersonalProductRepository(): PersonalProductRepository {
        val api: RestPersonalProductApiService = retrofit.create(
            RestPersonalProductApiService::class.java,
        )
        return PersonalProductRepository(api, hub.objectMapper)
    }

    private fun createAvailabilityRegistry(idRegistry: IdRegistry): AvailabilityRegistry {
        val ids: Collection<Long> = listOf(
            idRegistry.installmentFieldId,
            idRegistry.continueButtonId,
        )
        return AvailabilityRegistry(ids)
    }
}
