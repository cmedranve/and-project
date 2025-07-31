package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import androidx.core.util.Supplier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.factories.products.dashboard.hub.CardsHubFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.personal.cvv.SheetDialogCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForOtpVerification
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForPushOtpVerification
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.SheetDialogCoordinator
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro.DataStore
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.AtmCardAction
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.IntentionEvent
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.base.verification.OtpVerificationEvent
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.CardInfoForSetting
import pe.com.scotiabank.blpm.android.client.collections.CollectionsModel
import pe.com.scotiabank.blpm.android.client.debitcard.DebitCard
import pe.com.scotiabank.blpm.android.client.debitcard.PendingCard
import pe.com.scotiabank.blpm.android.client.gates.GateMapper
import pe.com.scotiabank.blpm.android.client.installments.InstallmentConstants
import pe.com.scotiabank.blpm.android.client.installments.selectoperation.SelectOperationActivity
import pe.com.scotiabank.blpm.android.client.model.AbstractMovementWrapperModel
import pe.com.scotiabank.blpm.android.client.model.BaseProductDetailModel
import pe.com.scotiabank.blpm.android.client.model.ErrorMovementWrapperModel
import pe.com.scotiabank.blpm.android.client.model.GateWrapperModel
import pe.com.scotiabank.blpm.android.client.model.NullMovementWrapperModel
import pe.com.scotiabank.blpm.android.client.model.collections.DebtRefinance
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushSuccess
import pe.com.scotiabank.blpm.android.client.products.detailproducts.NewProductDetailActivity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.GateWrapperEntity
import pe.com.scotiabank.blpm.android.data.exception.isForcingLogOut
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.UiEntityOfQuickActionCard
import java.lang.ref.WeakReference

class CardHubCoordinator(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    titleText: String,
    private val sheetDialogCoordinatorFactory: SheetDialogCoordinatorFactory,
    private val appModel: AppModel,
    private val model: CardsHubModel,
    private val cardSettingDetailModel: CardSettingModel,
    private val analyticModel: CardsHubAnalyticModel,
    private val collectionsModel: CollectionsModel,
    private val isDeepLink: Boolean,
    private val isRestrictedProfile: Boolean,
    private val cardsHubHelper: CardsHubHelper,
    private val dataStore: DataStore,
    private val isEnabledDebtRefinanceToCall: Boolean,
    private val isNewDetailScreen: Boolean,
    weakParent: WeakReference<out Coordinator?>,
    scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    mutableLiveHolder: MutableLiveHolder,
    userInterface: InstanceReceiver,
    uiStateHolder: UiStateHolder,
    override val id: Long = randomLong(),
) : CoordinatorImpl(
    weakParent = weakParent,
    scope = scope,
    dispatcherProvider = dispatcherProvider,
    mutableLiveHolder = mutableLiveHolder,
    userInterface = userInterface,
    uiStateHolder = uiStateHolder,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            UiEntityOfToolbar::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnToolbarIcon)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInRetryCreditCardsClicked),
            InstanceHandler(::onRetryCreditCardsClicked)
        )
        .add(
            UiEntityOfDoubleEndedImage::class,
            InstancePredicate(::filterGoDetailCreditCard),
            InstanceHandler(::handleClickOnDetailCreditCard)
        )
        .add(
            UiEntityOfDoubleEndedImage::class,
            InstancePredicate(::filterInPendingCreditCardOfDoubleEnded),
            InstanceHandler(::handleClickOnPendingCreditCardOfDoubleEnded)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInPendingCreditCardOfTextButton),
            InstanceHandler(::handleClickOnPendingCreditCardOfTextButton)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterShowCreditCardData),
            InstanceHandler(::handleClickOnShowCreditCardData)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterSettingsCreditCard),
            InstanceHandler(::handleClickOnSettingsCreditCard)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInRetryDebitCardsClicked),
            InstanceHandler(::onRetryDebitCardsClicked)
        )
        .add(
            UiEntityOfDoubleEndedImage::class,
            InstancePredicate(::filterGoDetailDebitCard),
            InstanceHandler(::handleClickOnDetailDebitCard)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterShowDebitCardData),
            InstanceHandler(::handleClickOnShowDebitCardData)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterSettingsDebitCard),
            InstanceHandler(::handleClickOnSettingsDebitCard)
        )
        .add(
            UiEntityOfDoubleEndedImage::class,
            InstancePredicate(::filterInPendingDebitCardOfDoubleEnded),
            InstanceHandler(::handleClickOnPendingDebitCardOfDoubleEnded)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInPendingDebitCardOfTextButton),
            InstanceHandler(::handleClickOnPendingDebitCardOfTextButton)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInRetryErrorHubClicked),
            InstanceHandler(::handleClickOnRetryHubSections)
        )
        .add(
            UiEntityOfQuickActionCard::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnGooglePayCard)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInDebitCardCreation),
            SuspendingHandlerOfInstance(::handleDebitCardCreation)
        )
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInCreditCardCreation),
            SuspendingHandlerOfInstance(::handleCreditCardCreation)
        )
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInShowCardDataAgain),
            SuspendingHandlerOfInstance(::handleShowCardDataAgain)
        )
        .add(
            AtmCardAction::class,
            InstancePredicate(::filterInCardSettings),
            SuspendingHandlerOfInstance(::handleCardSettings)
        )
        .add(
            OtpVerificationEvent::class,
            InstancePredicate(::filterInOtpVerified),
            SuspendingHandlerOfInstance(::handleOnOtpVerified)
        )
        .add(
            OtpPushSuccess::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleOnOtpPushVerified)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = true,
            iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_back,
            titleText = titleText,
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create(selfReceiver)

    private var retryCreditCardsEnabled: Boolean = true

    private var isCreditCardsError: Boolean = false
        set(value) {
            analyticModel.isCreditCardsErrorForAnalytics = value
            field = value
        }
    private var isDebitCardsError: Boolean = false
        set(value) {
            analyticModel.isDebitCardsErrorForAnalytics = value
            field = value
        }
    private var isCardsReload: Boolean = false
    private var numDebitCards: Int = Constant.ZERO
    private var isPendingCard: Boolean = false

    private val buttonIdOfReloadService: Long = randomLong()
    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(mainTopComposite::isErrorVisible)
        )
        .addCanvasButton(
            id = buttonIdOfReloadService,
            isEnabled = true,
            data = ReloadType.GO_RETRY_HUB_SECTIONS,
            text = weakResources.get()?.getString(R.string.hub_partial_error_button).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = toolbarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(mainBottomComposite),
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)
    private var sheetDialogCoordinator: SheetDialogCoordinator? = null

    private val _liveCompoundsOfSheetDialog: MutableLiveData<List<UiCompound<*>>> = MutableLiveData()
    override val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
        get() = _liveCompoundsOfSheetDialog

    override suspend fun start() = withContext(scope.coroutineContext) {
        isCardsReload = false
        loadCreditAndDebitCardsInParallel()
    }

    private fun sendAnalyticEvent(event: AnalyticEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        analyticModel.accept(eventData)
    }

    private suspend fun loadCreditAndDebitCardsInParallel() = withContext(mainDispatcher) {
        val creditCardResult: Deferred<Unit> = async { gettingCreditCards() }
        val debitCardResult: Deferred<Unit> = async { gettingDebitCards() }
        creditCardResult.await()
        debitCardResult.await()
        sendCardsViewAnalytics()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        receiveEvent(NavigationIntention.BACK)
        sendClickAnalyticsEvent(AnalyticsConstant.BACK)
    }

    private suspend fun gettingCreditCards() {
        mainTopComposite.compositeForCreditCard.currentState = UiState.LOADING
        updateUiData()

        tryCreditCardProducts(
            offline = true,
            productTypes = Constant.TC,
            transactional = false,
            hiddenProducts = false
        )
    }

    private suspend fun tryCreditCardProducts(
        offline: Boolean,
        productTypes: String,
        transactional: Boolean,
        hiddenProducts: Boolean,
    ) = try {

        val creditCards: List<NewProductModel> = model.getCreditCardList(
            offline = offline,
            productType = productTypes,
            transactional = transactional,
            hiddenProducts = hiddenProducts
        )

        onSuccessfulCreditCards(creditCards)
    } catch (throwable: Throwable) {
        if (throwable.isForcingLogOut) {
            showErrorMessage(throwable)
        } else {
            isCreditCardsError = true
            retryCreditCardsEnabled = true
            mainTopComposite.compositeForCreditCard.currentState = UiState.ERROR
            sendCardsErrorAnalytics(
                errorType = AnalyticsConstant.CREDIT + AnalyticsConstant.HYPHEN_STRING + AnalyticsConstant.ERROR,
            )
            validateCardsHubError()
        }
    }

    private fun sendCardsViewAnalytics() {
        val numDebitCards: Int = mainTopComposite.numDebitCards
        val numCreditCards: Int = mainTopComposite.numCreditCards
        val pendingCardsName: String = formatPendingCardsNameForAnalytics()
        val screenStatus: String = cardsHubHelper.validateTypeOfScreenCards(mainTopComposite)
        val extraLine: String = mainTopComposite.containExtraLine.toString()
        val products: List<NewProductModel> = mainTopComposite.compositeForCreditCard.composerOfHubCreditCard.fetchAll()
        val data: MutableMap<String, Any?> = mutableMapOf(
            AnalyticsConstant.SCREEN_STATUS to screenStatus,
            AnalyticsConstant.QUESTION_ONE to numDebitCards.toString(),
            AnalyticsConstant.QUESTION_TWO to numCreditCards.toString(),
            AnalyticsConstant.QUESTION_THREE to pendingCardsName,
            AnalyticsConstant.QUESTION_FOUR to extraLine,
            AnalyticsConstant.PRODUCTS to products,
        )
        sendAnalyticEvent(AnalyticEvent.SCREEN, data)
    }

    private fun formatPendingCardsNameForAnalytics(): String {
        val pendingCardNames: StringBuilder = StringBuilder()
        mainTopComposite.compositeForCreditCard.composerOfHubCreditCard.fetchAll()
            .filter { product -> product.isInactive }
            .forEach {  inactiveProduct -> pendingCardNames
                .append(inactiveProduct.name)
                .append(AnalyticsConstant.HYPHEN_STRING)
            }

        if (mainTopComposite.compositeForDebitCard.composerOfHubPendingDebitCard.fetchAll().isNotEmpty()) {
            pendingCardNames
                .append(Constant.DIGITAL_DEBIT_MASTERCARD)
                .append(AnalyticsConstant.HYPHEN_STRING)
        }

        if (pendingCardNames.isEmpty()) pendingCardNames.append(CardsHubFactory.ACTIVE) else pendingCardNames.append(CardsHubFactory.INACTIVE)

        return pendingCardNames.toString()
    }

    private fun sendCardsErrorAnalytics(errorType: String) {
        val data: MutableMap<String, Any?> = mutableMapOf(
            AnalyticsConstant.ERROR_TYPE to errorType,
            AnalyticsConstant.ERROR_MESSAGE to CardsHubFactory.PROBLEM_LOADING_INFORMATION,
        )
        sendAnalyticEvent(AnalyticEvent.ERROR_SCREEN, data)
    }

    private suspend fun onSuccessfulCreditCards(creditCards: List<NewProductModel>) {
        if (isCreditCardsError) {
            setCreditCardsError()
            return
        }

        mainTopComposite.compositeForCreditCard.composerOfHubCreditCard.clear()
        retryCreditCardsEnabled = true
        val currentState: UiState = UiState.from(creditCards.size)
        mainTopComposite.compositeForCreditCard.currentState = currentState
        validateCardsHubEmpty()

        if (currentState == UiState.EMPTY) {
            updateUiData()
            return
        }
        creditCards.forEach(mainTopComposite.compositeForCreditCard.composerOfHubCreditCard::add)
        updateUiData()
        showCvvIntro()

        doDeepLink(creditCards)
    }

    private suspend fun setCreditCardsError() {
        if (mainTopComposite.currentState == UiState.ERROR) {
            return
        }
        mainTopComposite.compositeForCreditCard.currentState = UiState.ERROR
        updateUiData()
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun showCvvIntro() {
        if (dataStore.isCvvOnboardingWasShown) return

        if (mainTopComposite.existsActiveDebitCards || mainTopComposite.existsActiveCreditCards) {
            weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_CVV_INTRO)
        }
    }

    private fun doDeepLink(creditCards: List<NewProductModel>) {
        if (isDeepLink.not()) return

        val creditCard: NewProductModel = getFirstTC(creditCards)
        val creditCardId: String = creditCard.id.toString()
        val subProductType: String = creditCard.subProductType ?: AnalyticsConstant.HYPHEN_STRING

        val carrier: CarrierOfActivityDestination = destinationCarrierOf(SelectOperationActivity::class.java) {
            InstallmentConstants.CREDIT_CARD_ID to creditCardId
            InstallmentConstants.DEEPLINK to creditCardId
            InstallmentConstants.ACCOUNT_TYPE to subProductType
        }
        userInterface.receive(carrier)
        appModel.clearRoutingEvent()
    }

    private fun getFirstTC(creditCards: List<NewProductModel>) = creditCards.first {
        it.productType == Constant.TC && it.subProductType != Constant.EL
    }

    private fun filterInRetryCreditCardsClicked(
        entity: UiEntityOfTextButton<*>,
    ): Boolean = ReloadType.GO_RETRY_CREDIT_CARDS == entity.data

    @Suppress("UNUSED_PARAMETER")
    private fun onRetryCreditCardsClicked(entity: UiEntityOfTextButton<*>) {
        if (retryCreditCardsEnabled.not()) return
        this.retryCreditCardsEnabled = false
        retryCreditCards()
    }

    private fun retryCreditCards() = scope.launch {
        sendClickAnalyticsEvent(eventLabel = AnalyticsConstant.CLICK_RETRY, atmCardType = AnalyticsConstant.CREDIT_CARD)
        isCreditCardsError = false
        gettingCreditCards()
    }

    private fun filterGoDetailCreditCard(entity: UiEntityOfDoubleEndedImage<*>): Boolean {
        val entityData = entity.data ?: return false
        val product: NewProductModel = entityData as? NewProductModel ?: return false
        return product.isInactive.not()
    }

    private fun handleClickOnDetailCreditCard(entity: UiEntityOfDoubleEndedImage<*>) = scope.launch {
        val entityData = entity.data ?: return@launch
        val product: NewProductModel = entityData as? NewProductModel ?: return@launch
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        val isExtraLine = Constant.EL.equals(product.subProductType, true)

        sendClickAnalyticsEvent(
            eventLabel = if (isExtraLine) CardsHubFactory.EXTRA_LINE_DETAIL else AnalyticsConstant.CARD_DETAIL,
            atmCardType = AnalyticsConstant.CREDIT_CARD,
            accountType = product.name,
            product = product,
        )
        tryGatesByProductType(product)
    }

    private suspend fun tryGatesByProductType(product: NewProductModel) = try {
        val gateWrapperEntity: GateWrapperEntity = model.getGatesByProductType(product.id)
        val gateWrapper: GateWrapperModel = GateMapper.transformGateWrapper(gateWrapperEntity)
        tryProductDetailV2(gateWrapper, product)
    } catch (throwable: Throwable) {
        if (throwable.isForcingLogOut) {
            showErrorMessage(throwable)
        } else {
            tryProductDetailV2(null, product)
        }
    }

    private suspend fun tryProductDetailV2(
        gateWrapper: GateWrapperModel?,
        product: NewProductModel,
    ) = try {
        if (isEnabledDebtRefinanceToCall) {
            loadProductDetailAndDebtRefinanceInParallel(
                product = product,
                gateWrapper = gateWrapper,
            )
        } else {
            val productDetail: BaseProductDetailModel = model.getProductDetailV2(
                productId = product.id,
            )
            setUpProductDetail(
                productDetail = productDetail,
                gateWrapper = gateWrapper,
                debtRefinance = null,
            )
        }
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun setUpProductDetail(
        productDetail: BaseProductDetailModel,
        gateWrapper: GateWrapperModel?,
        debtRefinance: DebtRefinance?,
    ) {
        when (productDetail.productType) {
            Constant.CC, Constant.TC, Constant.CTS -> {
                getMovements(
                    productDetail = productDetail,
                    gateWrapper = gateWrapper,
                    debtRefinance = debtRefinance,
                )
            }
            else -> showProductDetail(
                productDetail = productDetail,
                movementWrapper = NullMovementWrapperModel(),
                gateWrapper = gateWrapper,
                debtRefinance = debtRefinance,
            )
        }
    }

    private suspend fun loadProductDetailAndDebtRefinanceInParallel(
        product: NewProductModel,
        gateWrapper: GateWrapperModel?,
    ) = withContext(mainDispatcher) {
        val productDetailResult: Deferred<BaseProductDetailModel> = async {
            model.getProductDetailV2(
                productId = product.id,
            )
        }
        val debtRefinanceResult: Deferred<DebtRefinance?> = async {
            tryDebtRefinance(
                productId = product.id,
            )
        }

        val productDetail: BaseProductDetailModel = productDetailResult.await()
        val debtRefinance: DebtRefinance? = debtRefinanceResult.await()

        setUpProductDetail(
            productDetail = productDetail,
            gateWrapper = gateWrapper,
            debtRefinance = debtRefinance,
        )
    }

    private suspend fun tryDebtRefinance(
        productId: Long,
    ) = try {
        collectionsModel.getDebtRefinance(productId)
    } catch (throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
        null
    }

    private fun getMovements(
        productDetail: BaseProductDetailModel,
        gateWrapper: GateWrapperModel?,
        debtRefinance: DebtRefinance?
    ) = scope.launch {
        tryGetMovements(
            productDetail = productDetail,
            gateWrapper = gateWrapper,
            debtRefinance = debtRefinance,
        )
    }

    private suspend fun tryGetMovements(
        productDetail: BaseProductDetailModel,
        gateWrapper: GateWrapperModel?,
        debtRefinance: DebtRefinance?
    ) = try {
        val movementWrapper: AbstractMovementWrapperModel = model.getMovements(
            id = productDetail.id.toLong(),
            page = Constant.ZERO,
        )
        showProductDetail(
            productDetail = productDetail,
            movementWrapper = movementWrapper,
            gateWrapper = gateWrapper,
            debtRefinance = debtRefinance,
        )
    } catch (throwable: Throwable) {
        showProductDetail(
            productDetail = productDetail,
            movementWrapper = ErrorMovementWrapperModel(),
            gateWrapper = gateWrapper,
            debtRefinance = debtRefinance,
        )
    }

    private fun sendClickAnalyticsEvent(
        eventLabel: String,
        atmCardType: String = Constant.EMPTY_STRING,
        accountType: String = Constant.EMPTY_STRING,
        errorMessage: String = Constant.EMPTY_STRING,
        errorCode: String = Constant.EMPTY_STRING,
        errorType: String = Constant.EMPTY_STRING,
        product: NewProductModel = NewProductModel(),
    ) {
        val numDebitCards: Int = mainTopComposite.numDebitCards
        val numCreditCards: Int = mainTopComposite.numCreditCards
        val pendingCardsName: String = formatPendingCardsNameForAnalytics()
        val screenStatus: String = cardsHubHelper.validateTypeOfScreenCards(mainTopComposite)
        val extraLine: String = mainTopComposite.containExtraLine.toString()
        val data: MutableMap<String, Any?> = mutableMapOf(
            AnalyticsConstant.EVENT_LABEL to eventLabel,
            AnalyticsConstant.SCREEN_STATUS to screenStatus,
            AnalyticsConstant.QUESTION_ONE to numDebitCards.toString(),
            AnalyticsConstant.QUESTION_TWO to numCreditCards.toString(),
            AnalyticsConstant.PRODUCT_TYPE to atmCardType,
            AnalyticsConstant.ACCOUNT_TYPE to accountType,
            AnalyticsConstant.ERROR_MESSAGE to errorMessage,
            AnalyticsConstant.ERROR_CODE to errorCode,
            AnalyticsConstant.ERROR_TYPE to errorType,
            AnalyticsConstant.QUESTION_THREE to pendingCardsName,
            AnalyticsConstant.PRODUCT to product,
            AnalyticsConstant.QUESTION_FOUR to extraLine,
        )
        sendAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    private fun showProductDetail(
        productDetail: BaseProductDetailModel,
        movementWrapper: AbstractMovementWrapperModel,
        gateWrapper: GateWrapperModel?,
        debtRefinance: DebtRefinance?,
    ) {
        val carrierBuilder = CarrierOfActivityDestination.Builder(
                screenDestination = NewProductDetailActivity::class.java,
            )
            .putParcelableBy(
                idName = NewProductDetailActivity.PRODUCT_DETAIL_KEY,
                value = productDetail,
            )
            .putParcelableBy(
                idName = NewProductDetailActivity.MOVEMENT_WRAPPER_KEY,
                value = movementWrapper,
            )
            .putBooleanBy(
                idName = NewProductDetailActivity.IS_DEEP_LINK,
                value = false,
            )
            .putBooleanBy(
                idName = NewProductDetailActivity.IS_NEW_ACCOUNT,
                value = false,
            )

        gateWrapper?.let { nonNullGateWrapper ->
            carrierBuilder.putParcelableBy(
                idName = NewProductDetailActivity.PRODUCT_GATES,
                value = nonNullGateWrapper,
            )
        }

        debtRefinance?.let { nonNullDebtRefinance ->
            carrierBuilder.putParcelableBy(
                idName = NewProductDetailActivity.DEBT_REFINANCE_KEY,
                value = nonNullDebtRefinance,
            )
        }

        val carrier: CarrierOfActivityDestination = carrierBuilder.build()
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(carrier)
    }

    private fun filterInPendingCreditCardOfDoubleEnded(
        entity: UiEntityOfDoubleEndedImage<*>
    ): Boolean = entity.data?.let(::filterGoPendingCreditCard) ?: false

    private fun handleClickOnPendingCreditCardOfDoubleEnded(
        entity: UiEntityOfDoubleEndedImage<*>
    ) = entity.data?.let(::handleClickOnPendingCreditCard)

    private fun filterInPendingCreditCardOfTextButton(
        entity: UiEntityOfTextButton<*>
    ): Boolean = entity.data?.let(::filterGoPendingCreditCard) ?: false

    private fun handleClickOnPendingCreditCardOfTextButton(
        entity: UiEntityOfTextButton<*>
    ) = entity.data?.let(::handleClickOnPendingCreditCard)

    private fun filterGoPendingCreditCard(entityData: Any): Boolean {
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return false
        if (Action.ACTIVATE_CARD != atmCardAction.action) return false
        val product: NewProductModel = atmCardAction.data as? NewProductModel ?: return false
        return product.isInactive
    }

    private fun handleClickOnPendingCreditCard(entityData: Any) = scope.launch {

        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return@launch
        val product: NewProductModel = atmCardAction.data as? NewProductModel ?: return@launch
        sendClickAnalyticsEvent(
            eventLabel = atmCardAction.action.eventLabel,
            atmCardType = AnalyticsConstant.CREDIT_CARD,
            accountType = product.name,
        )
        weakParent.get()?.receiveFromChild(product)
    }

    private fun filterShowCreditCardData(entity: UiEntityOfTextButton<*>): Boolean {
        val entityData = entity.data ?: return false
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return false
        val action: Action = atmCardAction.action
        if (Action.SHOW_CARD_DATA != action) return false
        return atmCardAction.data is NewProductModel
    }

    private fun handleClickOnShowCreditCardData(entity: UiEntityOfTextButton<*>) = scope.launch {
        sheetDialogCoordinator?.receiveFromAncestor(IntentionEvent.CANCEL_SCOPE)
        val entityData = entity.data ?: return@launch
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return@launch
        val product: NewProductModel = atmCardAction.data as? NewProductModel ?: return@launch
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        sendClickAnalyticsEvent(
            eventLabel = atmCardAction.action.eventLabel,
            atmCardType = AnalyticsConstant.CREDIT_CARD,
            accountType = product.name,
        )

        if (isRestrictedProfile) {
            weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_RESTRICTED_PROFILE_ALERT)
            return@launch
        }

        if (product.isAvailable.not()) {
            checkCardSettingFlags(product)
            return@launch
        }

        val cardId: String = product.cardId
        tryGetCardSettings(cardId = cardId, card = product)
    }

    private suspend fun tryGetCardSettings(cardId: String, card: Any) {
        try {
            cardSettingDetailModel.apply(cardId)
            checkCardSettingFlags(card)
        } catch (throwable: Throwable) {
            showErrorMessage(throwable)
        }
    }

    private suspend fun checkCardSettingFlags(card: Any) {
        if (isInvalidCard(card)) {
            mutableLiveHolder.notifyMainLoadingVisibility(false)

            val cardSettingFlags = CardSettingFlags(
                isMainHolder = cardSettingDetailModel.isMainOrJoinHolder,
                isCardLocked = cardSettingDetailModel.isCardLocked,
                isPurchasesDisabled = cardSettingDetailModel.isPurchasesDisabled,
            )
            model.currentCard = card
            val atmCardInfo: AtmCardInfo = model.createAtmCardInfo(
                authId = String.EMPTY,
                authTracking = String.EMPTY,
                cardSettingFlags = cardSettingFlags,
            ) ?: return

            goToCardData(atmCardInfo)
            return
        }

        when(card) {
            is NewProductModel -> tryFetchingOperationIdCreditCard(card)
            is DebitCard -> tryFetchingOperationIdDebitCard(card)
            else -> Unit
        }
    }

    private fun isInvalidCard(card: Any): Boolean {
        val product = card as? NewProductModel
        return when {
            product?.isAvailable == false -> true
            cardSettingDetailModel.isCardLocked -> true
            cardSettingDetailModel.isPurchasesDisabled -> true
            else -> false
        }
    }

    private suspend fun tryFetchingOperationIdCreditCard(product: NewProductModel) = try {
        model.fetchOperationIdCreditCard(product)
        onSuccessFetchingOperationIdCreditCard(product.name)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private suspend fun onSuccessFetchingOperationIdCreditCard(accountType: String) {
        if (model.pushOtpFlowChecker.isPushOtpEnabled) {
            requirePushOtpVerification()
            return
        }

        model.sendOperationIdCreditCard()
        requireOtpVerification(accountType)
    }

    private fun requirePushOtpVerification() {
        val data = DataForPushOtpVerification(
            transactionId = model.operationId,
            analyticConsumer = analyticModel,
            analyticAdditionalData = Unit,
        )
        weakParent.get()?.receiveFromChild(data)
    }

    private fun requireOtpVerification(accountType: String) {
        val data = DataForOtpVerification(
            debitOperationId = model.operationId,
            accountType = accountType,
            questionOne = Constant.EMPTY_STRING,
            questionThree = Constant.EMPTY_STRING,
            productType = Constant.EMPTY_STRING,
            previousSectionDetail = Constant.EMPTY_STRING,
        )
        weakParent.get()?.receiveFromChild(data)
    }

    private fun filterInOtpVerified(
        event: OtpVerificationEvent,
    ): Boolean = OtpVerificationEvent.ON_DEBIT_CARD_OTP_VERIFIED == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleOnOtpVerified(event: OtpVerificationEvent) {
        val cardSettingFlags = CardSettingFlags(
            isMainHolder = cardSettingDetailModel.isMainOrJoinHolder,
            isCardLocked = cardSettingDetailModel.isCardLocked,
            isPurchasesDisabled = cardSettingDetailModel.isPurchasesDisabled,
        )
        val atmCardInfo: AtmCardInfo = model.createAtmCardInfo(
            authId = String.EMPTY,
            authTracking = String.EMPTY,
            cardSettingFlags = cardSettingFlags,
        ) ?: return

        goToCardData(atmCardInfo)
    }

    private suspend fun goToCardData(atmCardInfo: AtmCardInfo) {
        sheetDialogCoordinator = sheetDialogCoordinatorFactory.create(
            atmCardInfo = atmCardInfo,
            mutableLiveCompoundsOfSheetDialog = _liveCompoundsOfSheetDialog,
            weakParent = weakSelf,
        )
        sheetDialogCoordinator?.start()
    }

    private suspend fun handleOnOtpPushVerified(data: OtpPushSuccess) {

        mutableLiveHolder.notifyMainLoadingVisibility(true)

        val cardVerification = CardSettingFlags(
            isMainHolder = cardSettingDetailModel.isMainOrJoinHolder,
            isCardLocked = cardSettingDetailModel.isCardLocked,
            isPurchasesDisabled = cardSettingDetailModel.isPurchasesDisabled,
        )
        val atmCardInfo: AtmCardInfo = model.createAtmCardInfo(
            authId = data.authId,
            authTracking = data.authTracking,
            cardSettingFlags = cardVerification,
        ) ?: return

        goToCardData(atmCardInfo)
    }

    private fun filterSettingsCreditCard(entity: UiEntityOfTextButton<*>): Boolean {
        val entityData = entity.data ?: return false
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return false
        val action: Action = atmCardAction.action
        if (Action.CARD_SETTINGS != action) return false
        return atmCardAction.data is NewProductModel
    }

    private fun handleClickOnSettingsCreditCard(entity: UiEntityOfTextButton<*>) = scope.launch {
        val entityData = entity.data ?: return@launch
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return@launch
        val product: NewProductModel = atmCardAction.data as? NewProductModel ?: return@launch
        val cardId: String = product.cardId ?: return@launch
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        sendClickAnalyticsEvent(
            eventLabel = atmCardAction.action.eventLabel,
            atmCardType = AnalyticsConstant.CREDIT_CARD,
            accountType = product.name,
        )

        if (isRestrictedProfile) {
            weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_RESTRICTED_PROFILE_ALERT)
            return@launch
        }

        if (isNewDetailScreen) {

            val cardInfo = CardInfoForSetting(
                id = cardId,
                cardType = AtmCardType.CREDIT,
                name = product.name,
            )

            return@launch goToPersonalCardSettings(cardInfo)
        }

        tryGettingCardSettingsDetail(cardId)
    }

    private fun goToPersonalCardSettings(cardInfo: CardInfoForSetting) {
        weakParent.get()?.receiveFromChild(cardInfo)
    }

    private suspend fun tryGettingCardSettingsDetail(cardId: String) = try {
        val cardSettingDetail: Any = cardSettingDetailModel.apply(cardId)
        weakParent.get()?.receiveFromChild(cardSettingDetail)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun filterInRetryDebitCardsClicked(
        entity: UiEntityOfTextButton<*>,
    ): Boolean = ReloadType.GO_RETRY_DEBIT_CARDS == entity.data

    @Suppress("UNUSED_PARAMETER")
    private fun onRetryDebitCardsClicked(entity: UiEntityOfTextButton<*>) {
        isCardsReload = true
        retryDebitCards()
    }

    private fun retryDebitCards() = scope.launch {
        sendClickAnalyticsEvent(eventLabel = AnalyticsConstant.CLICK_RETRY, atmCardType = AnalyticsConstant.DEBIT_CARD)
        isDebitCardsError = false
        gettingDebitCards()
    }

    private suspend fun gettingDebitCards() {
        mainTopComposite.compositeForDebitCard.currentState = UiState.LOADING
        updateUiData()
        tryDebitCardProducts()
    }

    private suspend fun tryDebitCardProducts() = try {
        val debitCardHub: DebitCardHub = model.getDebitCardHub()
        val debitCards: List<DebitCard> = debitCardHub.cards
        val pendingCard: PendingCard? = debitCardHub.pendingCard

        numDebitCards = debitCards.size
        isPendingCard = pendingCard?.isCardCreationPending == true

        onSuccessfulDebitCards(debitCards)
        onSuccessfulPendingCard(pendingCard)

    } catch (throwable: Throwable) {
        if (throwable.isForcingLogOut) {
            showErrorMessage(throwable)
        } else {
            isDebitCardsError = true
            mainTopComposite.compositeForDebitCard.currentState = UiState.ERROR
            sendCardsErrorAnalytics(
                errorType = AnalyticsConstant.DEBIT + AnalyticsConstant.HYPHEN_STRING + AnalyticsConstant.ERROR,
            )
            validateCardsHubError()
        }
    }

    private suspend fun onSuccessfulDebitCards(debitCards: List<DebitCard>) {
        if (isDebitCardsError) {
            setDebitCardsError()
            return
        }

        mainTopComposite.compositeForDebitCard.composerOfHubDebitCard.clear()
        mainTopComposite.compositeForDebitCard.currentState = UiState.from(debitCards.size)
        debitCards.forEach(mainTopComposite.compositeForDebitCard.composerOfHubDebitCard::add)

        validateCardsHubEmpty()

        updateUiData()
        showCvvIntro()
    }

    private suspend fun setDebitCardsError() {
        if (mainTopComposite.currentState == UiState.ERROR) {
            return
        }
        mainTopComposite.compositeForDebitCard.currentState = UiState.ERROR
        updateUiData()
    }

    private suspend fun onSuccessfulPendingCard(pendingCard: PendingCard?) {
        if (isDebitCardsError) {
            setDebitCardsError()
            return
        }

        mainTopComposite.compositeForDebitCard.composerOfHubPendingDebitCard.removePendingCard()
        if (pendingCard == null || !pendingCard.isCardCreationPending) return

        mainTopComposite.compositeForDebitCard.composerOfHubPendingDebitCard.addPendingCard(pendingCard)
        mainTopComposite.compositeForDebitCard.currentState = UiState.SUCCESS

        validateCardsHubEmpty()

        updateUiData()
    }

    private fun filterGoDetailDebitCard(entity: UiEntityOfDoubleEndedImage<*>): Boolean {
        val entityData = entity.data ?: return false
        return entityData is DebitCard
    }

    private fun handleClickOnDetailDebitCard(entity: UiEntityOfDoubleEndedImage<*>) = scope.launch {

        val entityData = entity.data ?: return@launch

        val debitCard: DebitCard = entityData as? DebitCard ?: return@launch

        sendClickAnalyticsEvent(
            eventLabel = AnalyticsConstant.CARD_DETAIL,
            atmCardType = AnalyticsConstant.DEBIT_CARD,
            accountType = debitCard.name,
        )

        weakParent.get()?.receiveFromChild(debitCard)
    }

    private fun filterShowDebitCardData(entity: UiEntityOfTextButton<*>): Boolean {
        val entityData = entity.data ?: return false
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return false
        val action: Action = atmCardAction.action
        if (Action.SHOW_CARD_DATA != action) return false
        return atmCardAction.data is DebitCard
    }

    private fun handleClickOnShowDebitCardData(entity: UiEntityOfTextButton<*>) = scope.launch {
        sheetDialogCoordinator?.receiveFromAncestor(IntentionEvent.CANCEL_SCOPE)
        val entityData = entity.data ?: return@launch
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return@launch
        val debitCard: DebitCard = atmCardAction.data as? DebitCard ?: return@launch
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        sendClickAnalyticsEvent(
            eventLabel = atmCardAction.action.eventLabel,
            atmCardType = AnalyticsConstant.DEBIT_CARD,
            accountType = debitCard.name,
        )

        if (isRestrictedProfile) {
            weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_RESTRICTED_PROFILE_ALERT)
            return@launch
        }

        val cardId: String = debitCard.cardId
        tryGetCardSettings(cardId = cardId, card = debitCard)
    }

    private suspend fun tryFetchingOperationIdDebitCard(debitCard: DebitCard) = try {
        model.fetchOperationIdDebitCard(debitCard)
        onSuccessFetchingOperationIdDebitCard(debitCard.name)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private suspend fun onSuccessFetchingOperationIdDebitCard(accountType: String) {
        if (model.pushOtpFlowChecker.isPushOtpEnabled) {
            requirePushOtpVerification()
            return
        }

        model.sendOperationIdDebitCard()
        requireOtpVerification(accountType)
    }

    private fun filterSettingsDebitCard(entity: UiEntityOfTextButton<*>): Boolean {
        val entityData = entity.data ?: return false
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return false
        val action: Action = atmCardAction.action
        if (Action.CARD_SETTINGS != action) return false
        return atmCardAction.data is DebitCard
    }

    private fun handleClickOnSettingsDebitCard(entity: UiEntityOfTextButton<*>) = scope.launch {
        val entityData = entity.data ?: return@launch
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return@launch
        val debitCard: DebitCard = atmCardAction.data as? DebitCard ?: return@launch
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        sendClickAnalyticsEvent(
            eventLabel = atmCardAction.action.eventLabel,
            atmCardType = AnalyticsConstant.DEBIT_CARD,
            accountType = debitCard.name,
        )

        if (isRestrictedProfile) {
            weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_RESTRICTED_PROFILE_ALERT)
            return@launch
        }

        if (isNewDetailScreen) {

            val cardInfo = CardInfoForSetting(
                id = debitCard.cardId,
                cardType = AtmCardType.DEBIT,
                name = debitCard.name,
            )

            return@launch goToPersonalCardSettings(cardInfo)
        }

        tryGettingCardSettingsDetail(debitCard.cardId)
    }

    private fun filterInPendingDebitCardOfDoubleEnded(
        entity: UiEntityOfDoubleEndedImage<*>
    ): Boolean = entity.data?.let(::filterGoPendingDebitCard) ?: false

    private fun handleClickOnPendingDebitCardOfDoubleEnded(
        entity: UiEntityOfDoubleEndedImage<*>
    ) = entity.data?.let(::handleClickOnPendingDebitCard)

    private fun filterInPendingDebitCardOfTextButton(
        entity: UiEntityOfTextButton<*>
    ): Boolean = entity.data?.let(::filterGoPendingDebitCard) ?: false

    private fun handleClickOnPendingDebitCardOfTextButton(
        entity: UiEntityOfTextButton<*>
    ) = entity.data?.let(::handleClickOnPendingDebitCard)

    private fun filterGoPendingDebitCard(entityData: Any): Boolean {
        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return false
        if (Action.ACTIVATE_CARD != atmCardAction.action) return false
        return atmCardAction.data is PendingCard
    }

    private fun handleClickOnPendingDebitCard(entityData: Any) = scope.launch {

        val atmCardAction: AtmCardAction = entityData as? AtmCardAction ?: return@launch
        val pendingCard: PendingCard = atmCardAction.data as? PendingCard ?: return@launch
        sendClickAnalyticsEvent(
            eventLabel = atmCardAction.action.eventLabel,
            atmCardType = AnalyticsConstant.DEBIT_CARD,
            accountType = Constant.PENDING_CARD_NAME,
        )

        weakParent.get()?.receiveFromChild(pendingCard)
    }

    private suspend fun validateCardsHubError() {
        if (mainTopComposite.isFullScreenError) {
            mainTopComposite.currentState = UiState.ERROR
            clearHubSections()
            return
        }
        updateUiData()
    }

    private suspend fun validateCardsHubEmpty() {
        if (mainTopComposite.isFullScreenEmpty) {
            mainTopComposite.currentState = UiState.ERROR

            return
        }
        mainTopComposite.currentState = UiState.SUCCESS
        updateUiData()
    }

    private fun filterInRetryErrorHubClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = ReloadType.GO_RETRY_HUB_SECTIONS == entity.data

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnRetryHubSections(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        sendClickAnalyticsEvent(eventLabel = AnalyticsConstant.CLICK_RETRY)
        clearErrorFlags()
        reloadHub()
    }

    private fun clearErrorFlags() {
        isCreditCardsError = false
        isDebitCardsError = false
    }

    private suspend fun reloadHub() {
        mainTopComposite.currentState = UiState.EMPTY
        loadCreditAndDebitCardsInParallel()
    }

    private suspend fun clearHubSections() {
        mainTopComposite.compositeForDebitCard.currentState = UiState.EMPTY
        mainTopComposite.compositeForCreditCard.currentState = UiState.EMPTY
        updateUiData()
    }

    private fun filterInDebitCardCreation(
        event: IntentionEvent,
    ): Boolean = IntentionEvent.CARD_CREATED == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleDebitCardCreation(event: IntentionEvent) {
        mainTopComposite.compositeForDebitCard.composerOfHubDebitCard.clear()
        mainTopComposite.compositeForDebitCard.composerOfHubPendingDebitCard.removePendingCard()
        gettingDebitCards()
    }

    private fun filterInCreditCardCreation(
        event: IntentionEvent,
    ): Boolean = IntentionEvent.CREDIT_CARD_CREATED == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleCreditCardCreation(event: IntentionEvent) {
        mainTopComposite.compositeForCreditCard.composerOfHubCreditCard.clear()
        gettingCreditCards()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnGooglePayCard(
        entity: UiEntityOfQuickActionCard<*>
    ) = scope.launch {
        val eventLabel: String = CardsHubHelper.BANNER_ADD_TO_GOOGLE_WALLET
        sendClickAnalyticsEvent(eventLabel = eventLabel)
        weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_CARD_SETTING_HUB)
    }

    private fun filterInShowCardDataAgain(
        event: IntentionEvent,
    ): Boolean = IntentionEvent.SHOW_CARD_DATA_AGAIN == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleShowCardDataAgain(event: IntentionEvent) {
        val currentCard: Any = model.currentCard ?: return

        if (currentCard is NewProductModel) {
            mutableLiveHolder.notifyMainLoadingVisibility(true)
            tryFetchingOperationIdCreditCard(currentCard)
            return
        }

        if (currentCard is DebitCard) {
            mutableLiveHolder.notifyMainLoadingVisibility(true)
            tryFetchingOperationIdDebitCard(currentCard)
        }
    }

    private fun filterInCardSettings(
        atmCardAction: AtmCardAction,
    ): Boolean = Action.CARD_SETTINGS == atmCardAction.action

    private suspend fun handleCardSettings(atmCardAction: AtmCardAction) {
        val atmCardInfo: AtmCardInfo = atmCardAction.data as? AtmCardInfo ?: return
        val cardId: String = atmCardInfo.cardId
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        if (isRestrictedProfile) {
            weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_RESTRICTED_PROFILE_ALERT)
            return
        }

        if (isNewDetailScreen) {

            val cardInfo = CardInfoForSetting(
                id = cardId,
                cardType = atmCardInfo.atmCard.type,
                name = atmCardInfo.cardName,
            )

            return goToPersonalCardSettings(cardInfo)
        }

        tryGettingCardSettingsDetail(cardId)
    }
}
