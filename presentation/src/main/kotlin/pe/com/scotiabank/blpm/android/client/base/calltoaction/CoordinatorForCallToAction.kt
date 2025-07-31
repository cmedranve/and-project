package pe.com.scotiabank.blpm.android.client.base.calltoaction

import androidx.annotation.DrawableRes
import androidx.core.util.Consumer
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import java.lang.ref.WeakReference

class CoordinatorForCallToAction(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: Composite.Factory,
    factoryOfMainBottomComposite: Composite.Factory,
    private val availabilityRegistry: AvailabilityRegistry,
    private val analyticConsumer: Consumer<AnalyticEventData<*>>,
    private val embeddedDataName: String,
    private val analyticAdditionalData: Any,
    isToolbarEnabled: Boolean,
    @DrawableRes toolbarIconRes: Int,
    titleText: String,
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
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnCallToAction)
        )
        .build()

    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = isToolbarEnabled,
            iconRes = toolbarIconRes,
            titleText = titleText,
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: Composite = factoryOfMainTopComposite.create(
        receiver = selfReceiver,
        visibilitySupplier = Supplier(::isGoingToBeVisible),
    )

    private val mainBottomComposite: Composite = factoryOfMainBottomComposite.create(
        receiver = selfReceiver,
        visibilitySupplier = Supplier(::isGoingToBeVisible),
    )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = toolbarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(mainBottomComposite),
    )

    override suspend fun start() = withContext(scope.coroutineContext) {
        sendAnalyticEvent(AnalyticEvent.SCREEN)
        updateUiData()
    }

    private fun sendAnalyticEvent(event: AnalyticEvent, data: Map<String, Any?> = emptyMap()) {
        val additionalData: Map<String, Any> = mapOf(embeddedDataName to analyticAdditionalData)
        val eventData = AnalyticEventData(event, data.plus(additionalData))
        analyticConsumer.accept(eventData)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        sendClickEvent(AnalyticsConstant.BACK)
        receiveEvent(NavigationIntention.BACK)
    }

    private fun sendClickEvent(label: String) {
        val data: Map<String, Any?> = mapOf(AnalyticsConstant.EVENT_LABEL to label)
        sendAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    private fun handleClickOnCallToAction(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(entity.id)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)

        val callToAction: CallToAction = entity.data as? CallToAction
            ?: return@launch availabilityRegistry.setAvailabilityForAll(true)

        sendClickEvent(callToAction.analyticLabel)
        weakParent.get()?.receiveFromChild(callToAction)
        availabilityRegistry.setAvailabilityForAll(true)
    }
}
