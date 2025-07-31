package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.analytics.factories.products.dashboard.hub.CardsHubFactory
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.personal.cvv.SheetDialogCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro.DataStore
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.screen.CardSettingsMapper
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.screen.findTemplateForNewCardSettingsEntry
import pe.com.scotiabank.blpm.android.client.collections.CollectionsMapper
import pe.com.scotiabank.blpm.android.client.collections.CollectionsModel
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.ProfileTypeUtil
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.repository.NewGatesDataRepository
import pe.com.scotiabank.blpm.android.data.repository.collections.CollectionsRepository
import pe.com.scotiabank.blpm.android.data.repository.creditcard.CreditCardRepository
import pe.com.scotiabank.blpm.android.data.repository.debitcard.DebitCardRepository
import pe.com.scotiabank.blpm.android.data.repository.OldCardSettingsDataRepository
import pe.com.scotiabank.blpm.android.data.repository.cardsettings.CardSettingDataRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.ProductRepository
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.CollectorOfQuickActionCard
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.EmptyCollectorOfQuickActionCard
import java.lang.ref.WeakReference

class CardHubCoordinatorFactory(
    private val hub: Hub,
    private val titleText: String,
    private val isDeepLink: Boolean,
    private val credentialDataMapper: CredentialDataMapper,
    private val gatesDataRepository: NewGatesDataRepository,
    private val productRepository: ProductRepository,
    private val debitCardRepository: DebitCardRepository,
    private val creditCardRepository: CreditCardRepository,
    private val cardSettingsDataRepository: OldCardSettingsDataRepository,
    private val newCardSettingsDataRepository: CardSettingDataRepository,
    private val collectionRepository: CollectionsRepository,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    private val appModel: AppModel
        get() = hub.appModel

    fun create(): CardHubCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        val collectionsModel = CollectionsModel(
            dispatcherProvider = hub.dispatcherProvider,
            mapper = CollectionsMapper(),
            repository = collectionRepository,
        )

        val pushOtpFlowChecker = PushOtpFlowChecker(hub.appModel)

        val templateForNewCardSettings: OptionTemplate = findTemplateForNewCardSettingsEntry(
            navigation = appModel.navigationTemplate,
        )
        val isNewDetailScreen: Boolean = templateForNewCardSettings.isVisible && pushOtpFlowChecker.isPushOtpEnabled

        return CardHubCoordinator(
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(),
            factoryOfMainBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            weakResources = hub.weakResources,
            titleText = titleText,
            sheetDialogCoordinatorFactory = createSheetDialogCoordinatorFactory(),
            appModel = hub.appModel,
            model = createCardHubModel(credentialDataMapper, idRegistry, pushOtpFlowChecker),
            cardSettingDetailModel = pickCardSettingModel(isNewDetailScreen),
            analyticModel = createCardsHubAnalyticModel(),
            collectionsModel = collectionsModel,
            isDeepLink = isDeepLink,
            isRestrictedProfile = ProfileTypeUtil.checkRestrictedProfile(hub.appModel.profile),
            cardsHubHelper = CardsHubHelper(),
            dataStore = DataStore(hub.appContext),
            isEnabledDebtRefinanceToCall = isEnabledDebtRefinanceToCall(),
            isNewDetailScreen = isNewDetailScreen,
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

    private fun createFactoryOfMainTopComposite() = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
        factoryOfOneColumnTextEntity = hub.factoryOfOneColumnTextEntity,
        navigationTemplate = hub.appModel.navigationTemplate,
        collectorOfGooglePayQuickActionCard = pickCollectorOfGooglePayQuickActionCard(),
    )

    private fun pickCollectorOfGooglePayQuickActionCard(): CollectorOfQuickActionCard<*> {
        if (isGoogleWalletCardVisible()) {
            return CollectorOfGooglePayQuickActionCard(hub.weakResources, hub.appModel)
        }

        return EmptyCollectorOfQuickActionCard()
    }

    private fun isGoogleWalletCardVisible(): Boolean {
        val featureTemplate: FeatureTemplate = TemplatesUtil
            .getFeature(hub.appModel.navigationTemplate, TemplatesUtil.TOKENIZATION)
        val isFeatureOperationVisible: Boolean = TemplatesUtil
            .getOperation(featureTemplate, TemplatesUtil.ADD_CARDS_TO_GOOGLE_WALLET).isVisible

        if (isFeatureOperationVisible.not()) return false

        return hub.appModel.profile.showTokenizationCard
    }

    private fun createSheetDialogCoordinatorFactory() = SheetDialogCoordinatorFactory(
        hub = hub,
        credentialDataMapper = credentialDataMapper,
        debitCardRepository = debitCardRepository,
        creditCardRepository = creditCardRepository,
        parentScope = parentScope.newChildScope(),
    )

    private fun createCardHubModel(
        credentialDataMapper: CredentialDataMapper,
        idRegistry: IdRegistry,
        pushOtpFlowChecker: PushOtpFlowChecker,
    ): CardsHubModel = CardsHubModel(
        dispatcherProvider = hub.dispatcherProvider,
        pushOtpFlowChecker = pushOtpFlowChecker,
        appModel = hub.appModel,
        weakAppContext = hub.weakAppContext,
        gatesDataRepository = gatesDataRepository,
        productRepository = productRepository,
        debitCardRepository = debitCardRepository,
        creditCardRepository = creditCardRepository,
        mapper = CardsHubMapper(idRegistry, credentialDataMapper),
    )

    private fun pickCardSettingModel(isNewDetailScreen: Boolean): CardSettingModel {
        if (isNewDetailScreen) return createNewCardSettingModel()

        return createOldCardSettingModel()
    }

    private fun createNewCardSettingModel() = NewCardSettingModel(
        dispatcherProvider = hub.dispatcherProvider,
        repository = newCardSettingsDataRepository,
        mapper = CardSettingsMapper(),
    )

    private fun createOldCardSettingModel() = OldCardSettingModel(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
        repository = cardSettingsDataRepository,
    )

    private fun createCardsHubAnalyticModel(): CardsHubAnalyticModel {

        val analyticFactory = CardsHubFactory(
            systemDataFactory = hub.systemDataFactory,
            isGoogleWalletCardVisible = isGoogleWalletCardVisible(),
        )

        return CardsHubAnalyticModel(
            analyticsDataGateway = hub.analyticsDataGateway,
            analyticFactory = analyticFactory,
        )
    }

    private fun isEnabledDebtRefinanceToCall(): Boolean {
        val operation: OptionTemplate = TemplatesUtil.getOperation(
            navigation = hub.appModel.navigationTemplate,
            featureName = TemplatesUtil.PAYMENTS_AND_RECHARGE_KEY,
            optionName = TemplatesUtil.COLLECTIONS_REFINANCE_CARDS_KEY
        )
        return operation.isVisible
    }
}
