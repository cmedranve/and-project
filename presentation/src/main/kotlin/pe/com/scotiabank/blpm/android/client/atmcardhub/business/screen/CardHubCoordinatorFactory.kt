package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.cvv.SheetDialogCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro.DataStore
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen.CardSettingsMapper
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.BusinessOtpApiService
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import java.lang.ref.WeakReference

class CardHubCoordinatorFactory(
    private val hub: Hub,
    private val titleText: String,
    private val credentialDataMapper: CredentialDataMapper,
    private val businessCardRepository: BusinessCardsRepository,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(): CardHubCoordinator {

        val uiStateHolder: UiStateHolderWithErrorType = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        return CardHubCoordinator(
            factoryOfAppBarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(uiStateHolder),
            factoryOfMainBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            weakResources = hub.weakResources,
            titleText = titleText,
            sheetDialogCoordinatorFactory = createSheetDialogCoordinatorFactory(),
            idRegistry = idRegistry,
            model = createModel(idRegistry),
            dataStore = DataStore(hub.appContext),
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
        uiStateHolder: UiStateHolderWithErrorType
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        uiStateHolder = uiStateHolder,
        weakResources = hub.weakResources,
        factoryOfOneColumnTextEntity = hub.factoryOfOneColumnTextEntity,
    )

    private fun createSheetDialogCoordinatorFactory() = SheetDialogCoordinatorFactory(
        hub = hub,
        credentialDataMapper = credentialDataMapper,
        businessCardsRepository = businessCardRepository,
        parentScope = parentScope.newChildScope(),
    )

    private fun createModel(idRegistry: IdRegistry): CardHubModel = CardHubModel(
        dispatcherProvider = hub.dispatcherProvider,
        pushOtpFlowChecker = PushOtpFlowChecker(hub.appModel),
        repository = businessCardRepository,
        mapper = CardHubMapper(idRegistry, credentialDataMapper),
        cardSettingsMapper = CardSettingsMapper(),
        credentialDataMapper = credentialDataMapper,
        businessOtpRepository = createBusinessOtpRepository(),
    )

    private fun createBusinessOtpRepository(): BusinessOtpRepository {
        val apiService: BusinessOtpApiService = hub.appModel.sessionRetrofit.create(BusinessOtpApiService::class.java)
        return BusinessOtpRepository(apiService = apiService, objectMapper = hub.objectMapper)
    }
}
