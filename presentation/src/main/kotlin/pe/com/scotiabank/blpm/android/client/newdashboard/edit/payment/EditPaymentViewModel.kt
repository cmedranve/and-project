package pe.com.scotiabank.blpm.android.client.newdashboard.edit.payment

import android.content.res.Resources
import androidx.core.util.Supplier
import androidx.lifecycle.viewModelScope
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.LegacyViewModel
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.CarrierOfOperationEdited
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.copyFrom
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling
import pe.com.scotiabank.blpm.android.ui.util.KeyboardIntention
import java.lang.ref.WeakReference

class EditPaymentViewModel(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopCompositeForPayment.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    private val frequentOperation: FrequentOperationModel,
    private val model: EditPaymentModel,
    override val id: Long = randomLong(),
    private val mutableLiveHolder: MutableLiveHolder = MutableLiveHolder(),
    recycling: Recycling = StatefulRecycling(),
) : NewBaseViewModel(), LegacyViewModel, LiveHolder by mutableLiveHolder, Recycling by recycling {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            NavigationIntention::class,
            InstancePredicate(::filterInOnBackClicked),
            InstanceHandler(::notifyNavigationIntention)
        )
        .add(
            UiEntityOfToolbar::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnToolbarIcon)
        )
        .add(
            UiEntityOfEditText::class,
            InstancePredicate(::filterInEditableNameCleared),
            InstanceHandler(::onEditableNameCleared)
        )
        .add(
            UiEntityOfEditText::class,
            InstancePredicate(::filterInEditableNameEntered),
            InstanceHandler(::onEditableNameEntered)
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
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnSave)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = true,
            iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_back,
            titleText = weakResources.get()?.getString(R.string.title_activity_edit_frequent_payment).orEmpty(),
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: MainTopCompositeForPayment = factoryOfMainTopComposite.create(
        receiver = selfReceiver,
    )

    private val isButtonGoingToBeVisible = true
    private val saveButtonId: Long = randomLong()
    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isButtonGoingToBeVisible)
        )
        .addCanvasButton(
            id = saveButtonId,
            isEnabled = false,
            text = weakResources.get()?.getString(R.string.save).orEmpty(),
        )

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    override fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
        setUpUiData()
    }

    private fun setUpUiData() = viewModelScope.launch {
        putUiDataLaunchedByCoroutineScope()
        mainTopComposite.editableNameEntity?.text = frequentOperation.title.toString()
        putUiDataLaunchedByCoroutineScope()
        putEnablingButton()
        putUiDataLaunchedByCoroutineScope()
    }

    private fun putUiData() = viewModelScope.launch {
        putUiDataLaunchedByCoroutineScope()
    }

    private fun putEnablingButton() {
        mainBottomComposite.editCanvasButtonEnabling(id = saveButtonId, isEnabled = isToBeEnabled())
    }

    private suspend fun putUiDataLaunchedByCoroutineScope() {
        toolbarComposite.recomposeItselfIfNeeded()
        mainTopComposite.recomposeItselfIfNeeded()
        mainBottomComposite.recomposeItselfIfNeeded()

        mutableLiveHolder.notifyAppBarAndMain(
            appBar = toolbarComposite.compounds,
            mainTop = mainTopComposite.compounds,
            mainBottom = mainBottomComposite.compounds,
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        notifyNavigationIntention(NavigationIntention.BACK)
    }

    private fun filterInOnBackClicked(
        intention: NavigationIntention,
    ): Boolean = NavigationIntention.BACK == intention

    private fun notifyNavigationIntention(intention: NavigationIntention) {
        receiverOfViewModelEvents?.receive(intention)
    }

    private fun filterInEditableNameCleared(
        entity: UiEntityOfEditText<*>,
    ): Boolean = entity.text.isBlank()

    @Suppress("UNUSED_PARAMETER")
    private fun onEditableNameCleared(entity: UiEntityOfEditText<*>) {
        mainBottomComposite.editCanvasButtonEnabling(
            id = saveButtonId,
            isEnabled = false
        )
        putUiData()
    }

    private fun filterInEditableNameEntered(
        entity: UiEntityOfEditText<*>,
    ): Boolean = entity.text.isNotBlank()

    @Suppress("UNUSED_PARAMETER")
    private fun onEditableNameEntered(entity: UiEntityOfEditText<*>) {
        mainBottomComposite.editCanvasButtonEnabling(
            id = saveButtonId,
            isEnabled = isToBeEnabled()
        )
        putUiData()
    }

    private fun isToBeEnabled(): Boolean = mainTopComposite
        .editableNameEntity
        ?.text
        .isNullOrBlank()
        .not()

    private fun hideKeyboard() {
        receiverOfViewModelEvents?.receive(KeyboardIntention.HIDE)
    }

    private fun filterInImeAction(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.IME_ACTION_PRESSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleImeAction(carrier: InputEventCarrier<*, *>) {
        hideKeyboard()
    }

    private fun filterInBackKeyPressedWhenEditTextFocused(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.BACK_KEY_PRESSED_WHEN_EDIT_TEXT_FOCUSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleBackKeyPressedWhenEditTextFocused(carrier: InputEventCarrier<*, *>) {
        hideKeyboard()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnSave(entity: UiEntityOfCanvasButton<*>) = viewModelScope.launch {
        hideKeyboard()
        val newTitle: CharSequence = mainTopComposite.editableNameEntity?.text ?: return@launch

        setLoadingV2(true)

        val newFrequentOperation = FrequentOperationModel()
            .copyFrom(frequentOperation)
            .apply {
                title = newTitle.toString()
            }

        tryEdit(newFrequentOperation)
    }

    private suspend fun tryEdit(newFrequentOperation: FrequentOperationModel) = try {
        model.edit(newFrequentOperation)
        val carrier = CarrierOfOperationEdited(newFrequentOperation)
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(carrier)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    override fun receiveEvent(event: Any): Boolean = selfReceiver.receive(event)
}