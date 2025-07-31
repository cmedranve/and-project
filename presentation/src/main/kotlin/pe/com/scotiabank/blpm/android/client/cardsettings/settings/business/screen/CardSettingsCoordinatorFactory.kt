package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.number.DoubleParser
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import java.lang.ref.WeakReference

class CardSettingsCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
    private val businessCardsRepository: BusinessCardsRepository,
    private val businessOtpRepository: BusinessOtpRepository,
) {

    private val doubleParser: DoubleParser by lazy {
        DoubleParser(numberFormat = hub.generalNumberFormat)
    }

    private val holderOfCardSettings = MutableHolderOfCardSettings()

    fun create(card: AtmCardInfo): CardSettingsCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()

        val idRegistry = IdRegistry()

        val factoryOfMainTopComposite = createFactoryOfMainTopComposite(
            card = card,
            uiStateHolder = uiStateHolder,
            idRegistry = idRegistry,
        )

        val store: CardSettingStore = createStore(card, holderOfCardSettings)

        return CardSettingsCoordinator(
            factoryOfAppBarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = factoryOfMainTopComposite,
            factoryOfAnchoredBottomComposite = createFactoryOfMainBottomComposite(),
            factoryOfErrorBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            weakResources = hub.weakResources,
            uriHolder = UriHolder(hub.weakResources),
            model = createModel(card, holderOfCardSettings),
            store = store,
            shipmentFactory = CardSettingShipment.Factory(card, store),
            idRegistry = idRegistry,
            doubleParser = doubleParser,
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
        card: AtmCardInfo,
        uiStateHolder: UiStateHolder,
        idRegistry: IdRegistry,
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        defaultLocale = hub.defaultLocale,
        appModel = hub.appModel,
        weakResources = hub.weakResources,
        weakAppContext = hub.weakAppContext,
        card = card,
        uiStateHolder = uiStateHolder,
        idRegistry = idRegistry,
        doubleParser = doubleParser,
        factoryOfOneColumnTextEntity = hub.factoryOfOneColumnTextEntity,
    )

    private fun createFactoryOfMainBottomComposite() = BottomComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
    )

    private fun createModel(
        card: AtmCardInfo,
        holderOfCardSettings: MutableHolderOfCardSettings,
    ): CardSettingModel = CardSettingModel(
        dispatcherProvider = hub.dispatcherProvider,
        pushOtpFlowChecker = PushOtpFlowChecker(hub.appModel),
        card = card,
        cardRepository = businessCardsRepository,
        businessOtpRepository = businessOtpRepository,
        holderOfCardSettings = holderOfCardSettings,
        mapper = CardSettingsMapper()
    )

    private fun createStore(
        card: AtmCardInfo,
        holderOfCardSettings: MutableHolderOfCardSettings,
    ): CardSettingStore {

        val infoByCheckingMapper = InfoByCheckingMapper(cardType = card.atmCard.type)
        val storeFactory = CardSettingStore.Factory(
            weakResources = hub.weakResources,
            card = card,
            infoByCheckingMapper = infoByCheckingMapper,
            holderOfCardSettings = holderOfCardSettings,
            doubleParser = doubleParser,
        )

        return storeFactory.create()
    }
}