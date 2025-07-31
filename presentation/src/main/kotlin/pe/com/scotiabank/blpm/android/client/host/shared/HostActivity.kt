package pe.com.scotiabank.blpm.android.client.host.shared

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.DialogInterface.OnCancelListener
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.viewModels
import androidx.annotation.TransitionRes
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.core.util.Pair
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import br.com.hst.issuergp.core.IssuerGP
import br.com.hst.issuergp.core.IssuerGPUtil
import br.com.hst.issuergp.data.model.ProvisioningResult
import com.facephi.fphiwidgetcore.WidgetConfiguration
import com.facephi.selphi.Widget
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.material.datepicker.MaterialDatePicker
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyLargeTextType
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyTextType
import com.scotiabank.canvascore.bottomsheet.types.BodyLargeTextBottomSheetType
import com.scotiabank.canvascore.bottomsheet.types.BodyListBottomSheetType
import com.scotiabank.canvascore.bottomsheet.types.BodyTextBottomSheetType
import com.scotiabank.canvascore.dialog.CanvasDialogModal
import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
import com.scotiabank.canvascore.utils.dismissWithRunnable
import com.scotiabank.canvascore.views.CanvasSnackbar
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.applySecureSurfaceOnWindow
import pe.com.scotiabank.blpm.android.client.app.clearWindowFromSecureSurface
import pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.ThrowableWrapper
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.ComposerOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.DataHolderOfSheetDialogDismissing
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityNotFound
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfFragmentDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActionDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.FragmentOperation
import pe.com.scotiabank.blpm.android.client.base.carrier.feedFrom
import pe.com.scotiabank.blpm.android.client.base.carrier.putAllFrom
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEvent
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEventCarrier
import pe.com.scotiabank.blpm.android.client.base.canvassnackbar.CanvasSnackbarDataHolder
import pe.com.scotiabank.blpm.android.client.base.canvassnackbar.SnackbarAction
import pe.com.scotiabank.blpm.android.client.base.canvassnackbar.SnackbarEvent
import pe.com.scotiabank.blpm.android.client.base.canvassnackbar.SnackbarEventCarrier
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfCustomTabsIntentDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.DestinationDataHolder
import pe.com.scotiabank.blpm.android.client.host.GooglePlayEvent
import pe.com.scotiabank.blpm.android.client.identitydigitalvalidation.configuration.DataFromIdvConsumer
import pe.com.scotiabank.blpm.android.client.identitydigitalvalidation.shared.vendor.IDVWidgetEvent
import pe.com.scotiabank.blpm.android.client.identitydigitalvalidation.shared.vendor.IDVWidgetEventData
import pe.com.scotiabank.blpm.android.client.identitydigitalvalidation.shared.vendor.IDVWidgetReturnEvent
import pe.com.scotiabank.blpm.android.client.base.SearchIntention
import pe.com.scotiabank.blpm.android.client.base.datepicker.DatePickerDataHolder
import pe.com.scotiabank.blpm.android.client.base.datepicker.DatePickerEvent
import pe.com.scotiabank.blpm.android.client.base.datepicker.DatePickerEventCarrier
import pe.com.scotiabank.blpm.android.client.base.datepicker.setDatePickerDataHolder
import pe.com.scotiabank.blpm.android.client.base.dialog.StaticDataCanvasDialogModal
import pe.com.scotiabank.blpm.android.client.newmedallia.MedalliaAbstraction
import pe.com.scotiabank.blpm.android.client.newmedallia.result.MedalliaFormDataHolder
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.ManufacturerInspector
import pe.com.scotiabank.blpm.android.client.util.clipdata.ClipContent
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.lifecycle.LifecycleUtil
import pe.com.scotiabank.blpm.android.client.util.recyclerview.FactoryOfRecyclerView
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableHostBinding
import pe.com.scotiabank.blpm.android.ui.list.Composer
import pe.com.scotiabank.blpm.android.ui.list.ComposerOfAppBarAndMainPager
import pe.com.scotiabank.blpm.android.ui.list.LoadingObserver
import pe.com.scotiabank.blpm.android.ui.list.SpaceMeterForCoordinator
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import pe.com.scotiabank.blpm.android.ui.util.KeyboardIntention
import pe.com.scotiabank.blpm.android.ui.util.hideKeyboard
import pe.com.scotiabank.blpm.android.ui.util.showKeyboard
import java.lang.ref.WeakReference
import javax.inject.Inject

class HostActivity: BaseBindingActivity<ActivityPortableHostBinding>() {

    private val creationExtras = MutableCreationExtras()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: HostViewModel by viewModels(
        extrasProducer = ::creationExtras,
        factoryProducer = ::viewModelFactory,
    )

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            NavigationArrangement::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleNavigationArrangement)
        )
        .add(
            ObserverAction::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleRegisteringAgain)
        )
        .add(
            KeyboardIntention::class,
            InstancePredicate(KeyboardIntention::filterInHideKeyboard),
            InstanceHandler(::hideKeyboard)
        )
        .add(
            KeyboardIntention::class,
            InstancePredicate(KeyboardIntention::filterInShowKeyboard),
            InstanceHandler(::showKeyboard)
        )
        .add(
            ClipContent::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClipContent)
        )
        .add(
            NavigationIntention::class,
            InstancePredicate(NavigationIntention::filterInClose),
            InstanceHandler(::handleClose)
        )
        .add(
            SearchIntention::class,
            InstancePredicate(SearchIntention::filterInHide),
            InstanceHandler(::handleHide)
        )
        .add(
            ThrowableWrapper::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleThrowableWrapper)
        )
        .add(
            ModalDataHolder::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showCanvasDialogModal),
        )
        .add(
            AttrsBodyTextType::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showBottomSheetOfText)
        )
        .add(
            AttrsBodyLargeTextType::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showBottomSheetOfLargeText)
        )
        .add(
            StaticDataOfBottomSheetList::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showBottomSheetOfList)
        )
        .add(
            DataHolderOfSheetDialogDismissing::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::dismissBottomSheetOfList)
        )
        .add(
            CarrierOfActionDestination::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::openUriDestination),
        )
        .add(
            CarrierOfFragmentDestination::class,
            InstancePredicate(::filterInAddFragment),
            InstanceHandler(::addFragmentDestination)
        )
        .add(
            CarrierOfFragmentDestination::class,
            InstancePredicate(::filterInReplaceFragment),
            InstanceHandler(::replaceFragmentDestination)
        )
        .add(
            CarrierOfActivityDestination::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::goToActivityDestination)
        )
        .add(
            GooglePlayServicesRepairableException::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showDialogOnRepairableGooglePlay)
        )
        .add(
            GooglePlayEvent::class,
            InstancePredicate(GooglePlayEvent::filterInGooglePlayNotAvailable),
            InstanceHandler(::handleEventOnGooglePlayNotAvailable)
        )
        .add(
            PermissionRequestHolder::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleOnPermissionRequestHolder)
        )
        .add(
            CanvasSnackbarDataHolder::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showCanvasSnackbar),
        )
        .add(
            IDVWidgetEventData::class,
            InstancePredicate(IDVWidgetEvent::filterDocumentDataEvent),
            InstanceHandler(::handleIDVTakeDocumentPhoto)
        )
        .add(
            IDVWidgetEventData::class,
            InstancePredicate(IDVWidgetEvent::filterSelfieDataEvent),
            InstanceHandler(::handleIDVTakeSelfiePhoto)
        )
        .add(
            CarrierOfCustomTabsIntentDestination::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::launchCustomTabsIntent)
        )
        .add(
            Intent::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::launchImplicitIntent)
        )
        .add(
            ModalDataHolder::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showCanvasDialogModal)
        )
        .add(
            DatePickerDataHolder::class,
            InstancePredicate(::filterInDatePicker),
            InstanceHandler(::showDatePicker),
        )
        .add(
            DatePickerDataHolder::class,
            InstancePredicate(::filterInDateRangePicker),
            InstanceHandler(::showDateRangePicker),
        )
        .add(
            MedalliaFormDataHolder::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showMedalliaForm),
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val spaceMeter: SpaceMeterForCoordinator by lazy {
        SpaceMeterForCoordinator()
    }

    private val manufacturerInspector: ManufacturerInspector by lazy {
        ManufacturerInspector()
    }

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
                return
            }
            viewModel.receiveEvent(NavigationIntention.BACK)
        }
    }

    private val weakActivity: WeakReference<FragmentActivity?> by lazy {
        WeakReference(this)
    }

    private val medalliaAbstraction: MedalliaAbstraction by lazy {
        MedalliaAbstraction(viewModel)
    }

    private val googleApiAvailability: GoogleApiAvailability
        get() = GoogleApiAvailability.getInstance()

    private val launcherOfRepairableGooglePlay = registerLauncherOfRepairableGooglePlay()

    private fun registerLauncherOfRepairableGooglePlay(): ActivityResultLauncher<IntentSenderRequest> {
        val callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback(
            ::handleResultFromRepairableGooglePlay
        )
        return registerLauncherForIntentSender(callback)
    }

    private val launcherOfPermissionRequest = registerLauncherOfSinglePermission()

    private fun registerLauncherOfSinglePermission(): ActivityResultLauncher<String> {
        val callback: ActivityResultCallback<Boolean> = ActivityResultCallback(
            ::handleResultFromPermissionRequest
        )
        return registerLauncherForPermissionRequest(callback)
    }

    private val launcherOfFacephiDocumentWidget = registerLauncherOfFacephiDocumentWidget()

    private val launcherOfFacephiSelfieWidget = registerLauncherOfFacephiSelfieWidget()

    private val launcherOfUriDestination = registerLauncherOfUriDestination()

    private var mDataHolder: DestinationDataHolder? = null

    private fun registerLauncherOfUriDestination(): ActivityResultLauncher<Intent> {
        val callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback { result ->
            handleResultFromUriDestination(result)
        }
        return registerLauncherForActivityResult(callback)
    }

    private fun registerLauncherOfFacephiDocumentWidget(): ActivityResultLauncher<Intent> {
        val callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback(
            ::handleResultFromFacephiDocumentWidget
        )
        return registerLauncherForActivityResult(callback)
    }

    private fun registerLauncherOfFacephiSelfieWidget(): ActivityResultLauncher<Intent> {
        val callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback(
            ::handleResultFromFacephiSelfieWidget
        )
        return registerLauncherForActivityResult(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        @Suppress("UNUSED_VARIABLE") val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun getBindingInflater() = BindingInflaterOfActivity(ActivityPortableHostBinding::inflate)

    override fun getSettingToolbar(): Boolean = false

    override fun getToolbarTitle(): String = Constant.EMPTY_STRING

    override fun isOpenedSessionRequired(): Boolean = false

    override fun shouldExpireSession(): Boolean = false

    override fun additionalInitializer() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        addOnNewIntentListener(Consumer(::handleNewIntent))
        creationExtras.feedFrom(intent)
        spaceMeter.register(binding)
        setUpInstanceCollecting()
        setUpScreenshotCollecting()
        setUpMedalliaCollecting()
        viewModel.setUpUi(weakActivity, intent)
        intent = Intent()
    }

    private fun handleNewIntent(intent: Intent) {
        viewModel.receiveNewIntent(intent)
        this.intent = Intent()
    }

    private fun setUpInstanceCollecting() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = State.RESUMED, block = ::collectFromInstanceFlow)
        }
    }

    @Suppress("UNUSED_PARAMETER", "OPT_IN_USAGE")
    private suspend fun collectFromInstanceFlow(scope: CoroutineScope) {
        val instanceFlow: MutableSharedFlow<Any> = viewModel.instanceFlow
        instanceFlow.replayCache.forEach(selfReceiver::receive)
        instanceFlow.resetReplayCache()
        instanceFlow.onEach(::receiveInstance).collect()
    }

    @Suppress("OPT_IN_USAGE")
    private suspend fun receiveInstance(instance: Any) {
        selfReceiver.receive(instance)
        viewModel.instanceFlow.resetReplayCache()
    }

    private fun setUpObservers() {
        ComposerOfAppBarAndMainPager.compose(this, binding, viewModel)
        Composer.compose(this, binding.rvAnchoredBottomItems, viewModel.liveAnchoredBottomCompounds)
        LoadingObserver.observe(this, binding, viewModel)
    }

    private fun setUpScreenshotCollecting() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = State.RESUMED, block = ::collectFromScreenshotFlow)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun collectFromScreenshotFlow(scope: CoroutineScope) {
        val windowSecureFlagFlow: StateFlow<Boolean> = viewModel.windowSecureFlagFlow
        windowSecureFlagFlow.onEach(::onScreenshotEnabling).collect()
    }

    private fun onScreenshotEnabling(isEnabled: Boolean) {
        if (isEnabled) window.clearWindowFromSecureSurface() else window.applySecureSurfaceOnWindow()
        supportFragmentManager
            .fragments
            .forEach { fragment -> attemptEnablingScreenshot(fragment, isEnabled) }
    }

    private fun attemptEnablingScreenshot(fragment: Fragment, isEnabled: Boolean) {
        val dialogFragment: DialogFragment = fragment as? DialogFragment ?: return
        registerEnablingScreenshot(dialogFragment, isEnabled)
    }

    private fun registerEnablingScreenshot(dialogFragment: DialogFragment, isEnabled: Boolean) {
        val window: Window = dialogFragment.dialog?.window ?: return

        LifecycleUtil.runOnResume(dialogFragment) {
            if (isEnabled) window.clearWindowFromSecureSurface() else window.applySecureSurfaceOnWindow()
        }
    }

    private fun setUpMedalliaCollecting() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = State.RESUMED, block = ::collectFromMedalliaFlow)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun collectFromMedalliaFlow(scope: CoroutineScope) {
        val medalliaInterceptFlagFlow: StateFlow<Boolean> = viewModel.medalliaInterceptFlagFlow
        medalliaInterceptFlagFlow.onEach(::onMedalliaInterceptEnabling).collect()
    }

    private fun onMedalliaInterceptEnabling(isInterceptEnabled: Boolean) {
        if (isInterceptEnabled) medalliaAbstraction.enable() else medalliaAbstraction.disable()
    }

    private fun handleNavigationArrangement(arrangement: NavigationArrangement) {
        removeScrollListeners()
        removeObservers()
        if (ResourcesCompat.ID_NULL != arrangement.transitionResId) {
            beginDelayedTransition(arrangement.transitionResId)
        }
    }

    private fun removeScrollListeners() {
        binding.rvMainItems.clearOnScrollListeners()
        binding.rvResultItems.clearOnScrollListeners()
        binding.rvAnchoredBottomItems.clearOnScrollListeners()
    }

    private fun removeObservers() {
        viewModel.liveCompositeOfAppBarAndMain.removeObservers(this)
        viewModel.liveAnchoredBottomCompounds.removeObservers(this)
        viewModel.liveMainLoading.removeObservers(this)
        viewModel.liveResultLoading.removeObservers(this)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleRegisteringAgain(action: ObserverAction) {
        setUpObservers()
        if (shouldExpireSession()) appModel.startInactivityTimer()
    }

    private fun beginDelayedTransition(@TransitionRes transitionRedId: Int) {
        val transitionInflater: TransitionInflater = TransitionInflater.from(this)
        val transitionSet: Transition = transitionInflater.inflateTransition(transitionRedId) ?: return
        TransitionManager.beginDelayedTransition(binding.root, transitionSet)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun hideKeyboard(intention: KeyboardIntention) {
        val fragment: BodyListBottomSheetType? = supportFragmentManager.findFragmentByTag(
            BodyListBottomSheetType::class.simpleName
        ) as? BodyListBottomSheetType
        fragment?.dialog?.hideKeyboard() ?: hideKeyboard()
        currentFocus?.clearFocus()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun showKeyboard(intention: KeyboardIntention) {
        showKeyboard()
    }

    private fun handleClipContent(clipContent: ClipContent) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        clipboardManager.setPrimaryClip(clipContent.data)

        val isSamsung: Boolean = manufacturerInspector.isSamsungDevice()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || isSamsung) {
            showOnTextCopiedForLowerVersions(clipContent.messageForLowerVersions)
        }
    }

    private fun showOnTextCopiedForLowerVersions(message: String) {
        val fragment: BodyListBottomSheetType? = supportFragmentManager.findFragmentByTag(
            BodyListBottomSheetType::class.simpleName
        ) as? BodyListBottomSheetType

        val rootView: View = fragment?.view ?: binding.clEdgeToNavigationBar
        val canvasSnackBar = CanvasSnackbar.make(rootView)
        canvasSnackBar.setMessage(message)
        canvasSnackBar.show()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClose(intention: NavigationIntention) {
        finishAndRemoveTask()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleHide(intention: SearchIntention) {
        binding.searchView.hide()
    }

    private fun handleThrowableWrapper(wrapper: ThrowableWrapper) {
        showErrorMessage(wrapper.throwable)
    }

    private fun showLegacyAlertDialog(dataHolder: ModalDataHolder) {
        val attrs: AttrsCanvasDialogModal = dataHolder.attrs

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setMessage(attrs.textBody)

        if (attrs.primaryButtonLabel.isNotBlank()) {
            alertDialogBuilder
                .setPositiveButton(attrs.primaryButtonLabel) { _: DialogInterface?, _: Int ->
                    val carrier = ModalEventCarrier(ModalEvent.PRIMARY_CLICKED, dataHolder)
                    dataHolder.receiver?.receive(carrier)
                }
        }

        if (attrs.secondaryButtonLabel.isNotBlank()) {
            alertDialogBuilder
                .setNegativeButton(attrs.secondaryButtonLabel) { _: DialogInterface?, _: Int ->
                    val carrier = ModalEventCarrier(ModalEvent.SECONDARY_CLICKED, dataHolder)
                    dataHolder.receiver?.receive(carrier)
                }
        }

        if (attrs.title.isNotBlank()) {
            alertDialogBuilder.setTitle(attrs.title)
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @Suppress("unused")
    private fun showCanvasDialogModal(dataHolder: ModalDataHolder) {
        val modal: CanvasDialogModal = CanvasDialogModal.newInstance(dataHolder.attrs)
        modal.buttonDirectionColumn = dataHolder.buttonDirectionColumn
        registerModalCallbacks(dataHolder, modal)
        registerEnablingScreenshot(modal, viewModel.windowSecureFlagFlow.value)
        modal.show(supportFragmentManager, CanvasDialogModal.TAG_NAME)
    }

    private fun registerModalCallbacks(dataHolder: ModalDataHolder, modal: CanvasDialogModal) {
        modal.primaryButtonEvent = {
            val carrier = ModalEventCarrier(ModalEvent.PRIMARY_CLICKED, dataHolder)
            dataHolder.receiver?.receive(carrier)
        }
        modal.secondaryButtonEvent = {
            val carrier = ModalEventCarrier(ModalEvent.SECONDARY_CLICKED, dataHolder)
            dataHolder.receiver?.receive(carrier)
        }
    }

    private fun filterInDatePicker(
        dataHolder: DatePickerDataHolder,
    ): Boolean = dataHolder.defaultSelection.size == 1

    private fun showDatePicker(dataHolder: DatePickerDataHolder) {
        val timestamp: Long = dataHolder.defaultSelection.firstOrNull() ?: return
        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker.Builder.datePicker()
            .setSelection(timestamp)
            .setDatePickerDataHolder(dataHolder)
            .build()
        registerAnyDatePickerCallbacks(dataHolder, datePicker)
        registerEnablingScreenshot(datePicker, viewModel.windowSecureFlagFlow.value)
        datePicker.show(supportFragmentManager, "DatePicker")
    }

    private fun registerAnyDatePickerCallbacks(
        dataHolder: DatePickerDataHolder,
        datePicker: MaterialDatePicker<*>,
    ) {
        datePicker.addOnPositiveButtonClickListener {
            val event: DatePickerEvent = DatePickerEvent.POSITIVE_CLICKED
            val carrier = DatePickerEventCarrier(event, dataHolder, datePicker.selection)
            dataHolder.receiver?.receive(carrier)
        }
        datePicker.addOnNegativeButtonClickListener {
            val event: DatePickerEvent = DatePickerEvent.NEGATIVE_CLICKED
            val carrier = DatePickerEventCarrier(event, dataHolder, datePicker.selection)
            dataHolder.receiver?.receive(carrier)
        }
        datePicker.addOnCancelListener {
            val event: DatePickerEvent = DatePickerEvent.CANCEL_CLICKED
            val carrier = DatePickerEventCarrier(event, dataHolder, datePicker.selection)
            dataHolder.receiver?.receive(carrier)
        }
        datePicker.addOnDismissListener {
            val event: DatePickerEvent = DatePickerEvent.DISMISSED
            val carrier = DatePickerEventCarrier(event, dataHolder, datePicker.selection)
            dataHolder.receiver?.receive(carrier)
        }
    }

    private fun filterInDateRangePicker(
        dataHolder: DatePickerDataHolder,
    ): Boolean = dataHolder.defaultSelection.size == 2

    private fun showDateRangePicker(dataHolder: DatePickerDataHolder) {
        val startTimestamp: Long = dataHolder.defaultSelection.getOrNull(index = 0) ?: return
        val defaultEndTimestamp: Long = dataHolder.defaultSelection.getOrNull(index = 1) ?: return
        val pairedTimestamps: Pair<Long, Long> = Pair(startTimestamp, defaultEndTimestamp)
        val dateRangePicker: MaterialDatePicker<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()
            .setSelection(pairedTimestamps)
            .setDatePickerDataHolder(dataHolder)
            .build()
        registerAnyDatePickerCallbacks(dataHolder, dateRangePicker)
        registerEnablingScreenshot(dateRangePicker, viewModel.windowSecureFlagFlow.value)
        dateRangePicker.show(supportFragmentManager, "DateRangePicker")
    }

    private fun showBottomSheetOfText(attrs: AttrsBodyTextType) {
        val dialogFragment: BodyTextBottomSheetType = BodyTextBottomSheetType.newInstance(attrs)
        registerEnablingScreenshot(dialogFragment, viewModel.windowSecureFlagFlow.value)
        dialogFragment.show(supportFragmentManager)
    }

    private fun showBottomSheetOfLargeText(attrs: AttrsBodyLargeTextType) {
        val dialogFragment: BodyLargeTextBottomSheetType = BodyLargeTextBottomSheetType.newInstance(attrs)
        registerEnablingScreenshot(dialogFragment, viewModel.windowSecureFlagFlow.value)
        dialogFragment.show(supportFragmentManager)
    }

    private fun showBottomSheetOfList(data: StaticDataOfBottomSheetList) {
        val rvBsItems: RecyclerView = FactoryOfRecyclerView.create(this)
        ComposerOfBottomSheetList.compose(
            recyclerView = rvBsItems,
            fragmentManager = supportFragmentManager,
            staticData = data,
            liveCompounds = viewModel.liveCompoundsOfSheetDialog,
            isScreenshotEnabled = viewModel.windowSecureFlagFlow.value,
        )
    }

    private fun dismissBottomSheetOfList(dataHolder: DataHolderOfSheetDialogDismissing) {
        val fragment: BodyListBottomSheetType? = supportFragmentManager.findFragmentByTag(
            BodyListBottomSheetType::class.simpleName
        ) as? BodyListBottomSheetType

        fragment?.dismissWithRunnable(
            activity = this,
            actions = { viewModel.receiveEvent(dataHolder) },
            delayMillis = 0L,
        )
    }

    private fun openUriDestination(carrier: CarrierOfActionDestination) {
        val intent = Intent(carrier.action, carrier.uriDestination)
        carrier.extras.forEach(intent::putExtra)
        tryOpeningUriDestination(carrier, intent)
    }

    private fun tryOpeningUriDestination(carrier: CarrierOfActionDestination, intent: Intent) = try {
        mDataHolder = carrier.dataHolder
        launcherOfUriDestination.launch(intent)
    } catch (throwable: ActivityNotFoundException) {
        catchActivityNotFound(carrier, throwable)
    }

    private fun catchActivityNotFound(
        carrier: CarrierOfActionDestination,
        throwable: ActivityNotFoundException,
    ) {
        val receiver: InstanceReceiver = carrier.receiver ?: return
        val carrierOfActivityNotFound = CarrierOfActivityNotFound(throwable, carrier.data, carrier.id)
        receiver.receive(carrierOfActivityNotFound)
    }

    private fun launchCustomTabsIntent(carrier: CarrierOfCustomTabsIntentDestination) {
        val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(carrier.showTitle)
            .setShareState(carrier.shareState)
            .setInstantAppsEnabled(carrier.instantAppsEnabled)
            .build()
        val uri: Uri = carrier.uriDestination
        customTabsIntent.launchUrl(this, uri)
    }

    private fun launchImplicitIntent(intent: Intent) {
        startActivity(intent)
    }

    private fun showCanvasDialogModal(staticDataCanvasDialogModal: StaticDataCanvasDialogModal) {
        CanvasDialogModal.newInstance(staticDataCanvasDialogModal.attrsCanvasDialogModal).apply {
            primaryButtonEvent = {
                staticDataCanvasDialogModal.callbackOfPrimaryButton?.run()
            }
            secondaryButtonEvent = {
                staticDataCanvasDialogModal.callbackOfSecondaryButton?.run()
            }
        }.also { canvasDialogModal ->
            canvasDialogModal.show(supportFragmentManager, CanvasDialogModal.TAG_NAME)
        }
    }

    private fun filterInAddFragment(
        carrier: CarrierOfFragmentDestination,
    ): Boolean = FragmentOperation.ADD == carrier.operation

    @SuppressLint("CommitTransaction")
    private fun addFragmentDestination(carrier: CarrierOfFragmentDestination) {
        val fragmentContainerView: FragmentContainerView = binding.fragmentContainerView

        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.fragment_container_view,
                carrier.screenDestination,
                intent.extras,
            )
            .commitAllowingStateLoss()

        fragmentContainerView.visibility = View.VISIBLE
    }

    private fun filterInReplaceFragment(
        carrier: CarrierOfFragmentDestination,
    ): Boolean = FragmentOperation.REPLACE == carrier.operation

    @SuppressLint("CommitTransaction")
    private fun replaceFragmentDestination(carrier: CarrierOfFragmentDestination) {
        val fragmentContainerView: FragmentContainerView = binding.fragmentContainerView

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container_view,
                carrier.screenDestination,
                intent.extras,
            )
            .commitAllowingStateLoss()

        fragmentContainerView.visibility = View.VISIBLE
    }

    private fun goToActivityDestination(carrier: CarrierOfActivityDestination) {
        val intent = Intent(this, carrier.screenDestination)
            .putAllFrom(carrier)

        startActivity(intent)
    }

    private fun showDialogOnRepairableGooglePlay(throwable: GooglePlayServicesRepairableException) {
        googleApiAvailability.showErrorDialogFragment(
            this,
            throwable.connectionStatusCode,
            launcherOfRepairableGooglePlay,
            OnCancelListener(::handleDialogDismissOnRepairableGooglePlay),
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleDialogDismissOnRepairableGooglePlay(dialog: DialogInterface) {
        showDialogOnGooglePlayNotAvailable()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleResultFromRepairableGooglePlay(result: ActivityResult) {
        viewModel.receiveEvent(GooglePlayEvent.ATTEMPT_TO_REMEDIATE)
    }

    private fun handleResultFromPermissionRequest(granted: Boolean) {
        viewModel.receiveEvent(granted)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleResultFromUriDestination(
        result: ActivityResult
    ) {
        mDataHolder?.let(viewModel::receiveEvent)
    }

    private fun showDialogOnGooglePlayNotAvailable() {
        val message: String = getString(R.string.exception_message_generic)
        showMessageSession(message)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleEventOnGooglePlayNotAvailable(event: GooglePlayEvent) {
        showDialogOnGooglePlayNotAvailable()
    }

    private fun handleOnPermissionRequestHolder(permissionRequestHolder: PermissionRequestHolder) {
        val permission: String = permissionRequestHolder.permission
        launcherOfPermissionRequest.launch(permission)
    }

    @Suppress("unused")
    private fun showCanvasSnackbar(dataHolder: CanvasSnackbarDataHolder) {
        val message: SpannableStringBuilder = dataHolder.message
        val action: SnackbarAction? = dataHolder.action
        val icon = dataHolder.icon

        val canvasSnackBar = CanvasSnackbar
            .make(binding.clEdgeToNavigationBar)

        canvasSnackBar.apply {
            setMessage(
                text = message
            )
            setIcon(
                icon = icon
            )
            action?.let {
                setAction(
                    text = getString(action.text),
                    image = action.image,
                    isPositionBottom = action.isPositionBottom,
                    isPositionRight = action.isPositionRight,
                )
                actionButtonEvent = { registerSnackbarCallback(dataHolder) }
            }
        }

        canvasSnackBar.show()
    }

    private fun registerSnackbarCallback(dataHolder: CanvasSnackbarDataHolder) {
        val carrier = SnackbarEventCarrier(SnackbarEvent.ACTION_CLICKED, dataHolder)
        dataHolder.receiver?.receive(carrier)
    }

    private fun handleIDVTakeDocumentPhoto(eventData: IDVWidgetEventData<*>) { }

    private fun handleResultFromFacephiDocumentWidget(result: ActivityResult) {
        if (result.resultCode == RESULT_CANCELED) {
            resolveResultCanceled()
            return
        }
        val extras: Bundle = result.data?.extras ?: return
        val eventData = IDVWidgetEventData(IDVWidgetReturnEvent.RETURN_FROM_DOCUMENT_WIDGET, extras)
        viewModel.receiveEvent(eventData)
    }

    private fun resolveResultCanceled() {
        val eventData = IDVWidgetEventData(IDVWidgetReturnEvent.RETURN_FROM_WIDGET_BACK_ACTION)
        viewModel.receiveEvent(eventData)
    }

    private fun handleIDVTakeSelfiePhoto(eventData: IDVWidgetEventData<*>) {
        val configuration: WidgetConfiguration = eventData.data as? WidgetConfiguration ?: return
        val intent = Intent(this, Widget::class.java)
            .putExtra(DataFromIdvConsumer.FACEPHI_CONFIGURATION, configuration)
        launcherOfFacephiSelfieWidget.launch(intent)
    }

    private fun handleResultFromFacephiSelfieWidget(result: ActivityResult) {
        val extras: Bundle? = result.data?.extras
        if (result.resultCode == RESULT_CANCELED) {
            resolveResultCanceled(extras)
            return
        }
        val eventData = IDVWidgetEventData(IDVWidgetReturnEvent.RETURN_FROM_SELFIE_WIDGET, extras)
        viewModel.receiveEvent(eventData)
    }

    private fun resolveResultCanceled(extras: Bundle?) {
        val eventData = IDVWidgetEventData(IDVWidgetReturnEvent.RETURN_FROM_WIDGET_CANCELED_ACTION, extras)
        viewModel.receiveEvent(eventData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != IssuerGP.REQUEST_PUSH_TOKENIZE) return

        val provisioningResult: ProvisioningResult = IssuerGPUtil.retrieveProvisioningResult(
            resultCode = resultCode,
            data = data
        )
        viewModel.receiveEvent(provisioningResult)
    }

    private fun showMedalliaForm(dataHolder: MedalliaFormDataHolder) {
        medalliaAbstraction.showForm(dataHolder)
    }

    override fun onPause() {
        medalliaAbstraction.disable()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.recyclingState = binding.rvMainItems.layoutManager?.onSaveInstanceState()
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        spaceMeter.unregister()
        super.onDestroy()
    }
}