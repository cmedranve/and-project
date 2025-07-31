package pe.com.scotiabank.blpm.android.client.base.verification

import android.content.res.Resources
import androidx.core.util.Supplier
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.encoding.blankOut
import com.scotiabank.enhancements.encoding.mapToCharArray
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import com.scotiabank.errorhandling.StoreOfSuspendingErrorHandling
import com.scotiabank.errorhandling.SuspendingReceiverOfError
import com.scotiabank.errorhandling.SuspendingReceivingAgentOfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEvent
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEventCarrier
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.Channel
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.ChannelRegistry
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.FactoryOfModalDataHolder
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.NumberInput
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.exception.ForceUpdateException
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiEntityOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import java.lang.ref.WeakReference
import java.net.ConnectException
import java.net.SocketException

class OtpVerificationCoordinator(
    titleText: CharSequence,
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    channelRegistry: ChannelRegistry,
    numberInput: NumberInput,
    private val idRegistry: IdRegistry,
    private val availabilityRegistry: AvailabilityRegistry,
    private val newOtpRequestModel: SuspendingFunction<CharArray, Unit>,
    private val otpVerificationModel: SuspendingFunction<CharArray, Any>,
    private val errorTextOnFieldRequired: CharSequence,
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
            UiEntityOfEditText::class,
            InstancePredicate(::filterInOtpCleared),
            InstanceHandler(::onOtpCleared)
        )
        .add(
            UiEntityOfEditText::class,
            InstancePredicate(::filterInOtpEntered),
            InstanceHandler(::onOtpEntered)
        )
        .add(
            InputEventCarrier::class,
            InstancePredicate(::filterInImeAction),
            InstanceHandler(::handleImeAction)
        )
        .add(
            InputEventCarrier::class,
            InstancePredicate(::filterInBackKeyPressedWhenEditTextFocused),
            InstanceHandler(::handleBackKeyPressedWhenEditTextFocused)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInClickOnRetry),
            InstanceHandler(::handleClickOnRetry)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInClickOnSendBy),
            InstanceHandler(::handleClickOnSendBy)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnContinue)
        )
        .add(
            ModalEventCarrier::class,
            InstancePredicate(::filterInExceededAttempts),
            InstanceHandler(::handleExceededAttempts)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val storeOfSuspendingErrorHandling: StoreOfSuspendingErrorHandling = StoreOfSuspendingErrorHandling.Builder()
        .addHandlerByInstance(
            HttpResponseException::class,
            InstancePredicate(::filterInAttemptErrorOnOtpSent),
            SuspendingHandlerOfInstance(::handleAttemptErrorOnOtpSent),
        )
        .putHandlerByType(
            FinishedSessionException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .putHandlerByType(
            ForceUpdateException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .putHandlerByType(
            SocketException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .putHandlerByType(
            ConnectException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .setHandlerForCatchingAll(
            SuspendingHandlerOfInstance(FirebaseCrashlytics.getInstance()::recordException),
        )
        .build()

    private val selfSuspendingReceiverOfError: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = storeOfSuspendingErrorHandling,
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

    private val entityOfOtpInput: UiEntityOfEditText<NumberInput>?
        get() = mainTopComposite.findNumberInputBy(id = idRegistry.idOfOtpInput)

    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(uiStateHolder::isSuccessVisible),
        )
        .addCanvasButton(
            id = idRegistry.idOfContinueButton,
            isEnabled = false,
            text = weakResources.get()?.getString(R.string.btn_continue).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = toolbarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(mainBottomComposite),
    )

    private val factoryOfModalDataHolder: FactoryOfModalDataHolder = FactoryOfModalDataHolder(
        dispatcherProvider = dispatcherProvider,
        weakResources = weakResources,
        channelRegistry = channelRegistry,
        receiver = selfReceiver,
    )

    private val factoryOfInspectedOtpModalDataHolder = FactoryOfInspectedOtpModalDataHolder(
        dispatcherProvider = dispatcherProvider,
        weakResources = weakResources,
        receiver = selfReceiver,
    )

    init {
        mainTopComposite.clearThenAddChannel(channelRegistry.default)
        mainTopComposite.addNumberInput(numberInput = numberInput, id = idRegistry.idOfOtpInput)
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        uiStateHolder.currentState = UiState.SUCCESS
        updateUiData()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) = scope.launch {
        hideKeyboard()
        receiveEvent(NavigationIntention.BACK)
    }

    private fun filterInOtpCleared(
        entity: UiEntityOfEditText<*>,
    ): Boolean = entity.text.isBlank()

    private fun onOtpCleared(entity: UiEntityOfEditText<*>) = scope.launch {
        entity.errorText = Constant.EMPTY_STRING
        disableContinueButton()
        updateUiData()
    }

    private fun disableContinueButton() {
        mainBottomComposite.editCanvasButtonEnabling(
            id = idRegistry.idOfContinueButton,
            isEnabled = false,
        )
    }

    private fun filterInOtpEntered(
        entity: UiEntityOfEditText<*>,
    ): Boolean = entity.text.isNotBlank()

    private fun onOtpEntered(entity: UiEntityOfEditText<*>) = scope.launch {
        val numberInput: NumberInput = entity.data as? NumberInput ?: return@launch
        val isAllowed: Boolean = numberInput.isLengthAllowed(entity.text.trim())
        entity.errorText = if (isAllowed) Constant.EMPTY_STRING else errorTextOnFieldRequired
        mainBottomComposite.editCanvasButtonEnabling(
            id = idRegistry.idOfContinueButton,
            isEnabled = isToBeEnabled(),
        )
        updateUiData()
    }

    private fun isToBeEnabled(): Boolean {
        val otpEntity: UiEntityOfEditText<NumberInput> = entityOfOtpInput ?: return false
        val numberInput: NumberInput = otpEntity.data ?: return false
        return numberInput.isLengthAllowed(otpEntity.text.trim())
    }

    private fun filterInImeAction(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.IME_ACTION_PRESSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleImeAction(carrier: InputEventCarrier<*, *>) = scope.launch {
        hideKeyboard()
    }

    private fun filterInBackKeyPressedWhenEditTextFocused(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.BACK_KEY_PRESSED_WHEN_EDIT_TEXT_FOCUSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleBackKeyPressedWhenEditTextFocused(
        carrier: InputEventCarrier<*, *>,
    ) = scope.launch {
        hideKeyboard()
    }

    private fun filterInClickOnRetry(
        entity: UiEntityOfTextButton<*>,
    ): Boolean = entity.id == idRegistry.idOfRetrying

    private fun handleClickOnRetry(entity: UiEntityOfTextButton<*>) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.idOfRetrying)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)
        hideKeyboard()

        val channel: Channel = entity.data as? Channel
            ?: return@launch availabilityRegistry.setAvailabilityForAll(true)

        mutableLiveHolder.notifyMainLoadingVisibility(true)

        tryRetrying(channel)
    }

    private suspend fun tryRetrying(channel: Channel) = try {
        newOtpRequestModel.apply(channel.typeForPeruApiCall.mapToCharArray())
        onRetryFromServer(channel)
        availabilityRegistry.setAvailabilityForAll(true)
    } catch (throwable: Throwable) {
        selfSuspendingReceiverOfError.receive(throwable)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private suspend fun onRetryFromServer(channel: Channel) {
        entityOfOtpInput?.let(::clearInputText)
        disableContinueButton()
        mainTopComposite.clearThenAddChannel(channel)
        val modalDataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(channel)
        updateUiData()
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(modalDataHolder)
    }

    private fun clearInputText(entity: UiEntityOfInputText<*>) {
        entity.text = Constant.EMPTY_STRING
        entity.errorText = Constant.EMPTY_STRING
    }

    private fun filterInClickOnSendBy(
        entity: UiEntityOfTextButton<*>,
    ): Boolean = entity.id == idRegistry.idOfSendingBy

    private fun handleClickOnSendBy(entity: UiEntityOfTextButton<*>) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.idOfSendingBy)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)
        hideKeyboard()

        val channel: Channel = entity.data as? Channel
            ?: return@launch availabilityRegistry.setAvailabilityForAll(true)

        mutableLiveHolder.notifyMainLoadingVisibility(true)
        val otherChannel: Channel = channel.swap()
        tryRetrying(otherChannel)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnContinue(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.idOfContinueButton)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)
        hideKeyboard()

        val otpEntity: UiEntityOfEditText<NumberInput> = entityOfOtpInput
            ?: return@launch availabilityRegistry.setAvailabilityForAll(true)

        mutableLiveHolder.notifyMainLoadingVisibility(true)

        val otpCode: CharArray = otpEntity.text.trim().mapToCharArray()
        tryVerifyingOtp(otpCode)
    }

    private suspend fun tryVerifyingOtp(otpCode: CharArray) = try {
        val successfulVerification: Any = otpVerificationModel.apply(otpCode)
        otpCode.blankOut()
        onOtpVerified(successfulVerification)
        availabilityRegistry.setAvailabilityForAll(true)
    } catch (throwable: Throwable) {
        otpCode.blankOut()
        selfSuspendingReceiverOfError.receive(throwable)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private fun filterInAttemptErrorOnOtpSent(throwable: HttpResponseException): Boolean {
        val code: String = throwable.body?.code?.let(::String)?.uppercase() ?: return false

        return Constant.ERROR_FIRST_TRY.contentEquals(code)
                || Constant.ERROR_THIRD_TRY.contentEquals(code)
    }

    private suspend fun handleAttemptErrorOnOtpSent(throwable: HttpResponseException) {
        val code: String = throwable.body?.code?.let(::String)?.uppercase() ?: return

        val modalDataHolder: ModalDataHolder = factoryOfInspectedOtpModalDataHolder.createBy(
            code = code,
            message = throwable.body?.message ?: charArrayOf(),
        )
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(modalDataHolder)
    }

    private fun filterInExceededAttempts(modalEventCarrier: ModalEventCarrier): Boolean {
        val code: String = modalEventCarrier.dataHolder.data as? String ?: return false
        return ModalEvent.PRIMARY_CLICKED == modalEventCarrier.event
                && Constant.ERROR_THIRD_TRY == code
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleExceededAttempts(modalEventCarrier: ModalEventCarrier) {
        receiveEvent(NavigationIntention.BACK)
    }

    private suspend fun onOtpVerified(successfulVerification: Any) {
        entityOfOtpInput?.let(::clearInputText)
        disableContinueButton()
        weakParent.get()?.receiveFromChild(successfulVerification)
    }
}
