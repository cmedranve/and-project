package pe.com.scotiabank.blpm.android.client.base.products.newpicking.bottomsheet

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.products.ProductMapper
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.IdRegistry
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.Mapper
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.ProductPickingModel
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton.CollectorOfInstallmentChipsComponent
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.payment.PaymentUtil
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.net.RestBusinessProductApiService
import pe.com.scotiabank.blpm.android.data.net.RestPersonalProductApiService
import pe.com.scotiabank.blpm.android.data.repository.products.stable.BusinessProductRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.PersonalProductRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.ProductRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class ProductPickingSheetCoordinatorFactory(
    private val hub: Hub,
    private val retrofit: Retrofit,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    private val appModel: AppModel
        get() = hub.appModel

    fun create(
        isPayableWithCreditCard: Boolean,
        titleText: String = String.EMPTY,
    ): ProductPickingSheetCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()
        val factoryOfMainTopComposite: ProductPickingSheetComposite.Factory = createFactoryOfMainTopComposite(
            uiStateHolder = uiStateHolder,
        )

        return ProductPickingSheetCoordinator(
            titleText = titleText,
            factoryOfComposite = factoryOfMainTopComposite,
            weakResources = hub.weakResources,
            availabilityRegistry = createAvailabilityRegistry(idRegistry),
            model = createProductPickingModel(isPayableWithCreditCard),
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfMainTopComposite(
        uiStateHolder: UiStateHolder,
    ) = ProductPickingSheetComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        formatterOfProductName = hub.formatterOfProductName,
        factoryOfOneColumnTextEntity = hub.factoryOfOneColumnTextEntity,
        installmentCollector = CollectorOfInstallmentChipsComponent(hub.weakAppContext),
        uiStateHolder = uiStateHolder,
    )

    private fun createAvailabilityRegistry(idRegistry: IdRegistry): AvailabilityRegistry {
        val ids: Collection<Long> = listOf(
            idRegistry.continueButtonId,
        )
        return AvailabilityRegistry(ids)
    }

    private fun createProductPickingModel(isPayableWithCreditCard: Boolean): ProductPickingModel {
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
}
