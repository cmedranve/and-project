package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import java.lang.ref.WeakReference

class CvvIntroCoordinator(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    private val idRegistry: IdRegistry,
    private val availabilityRegistry: AvailabilityRegistry,
    private val dataStore: DataStore,
    private val analyticModel: AnalyticModel,
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
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnUnderstood)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = false,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create()

    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isGoingToBeVisible),
        )
        .addCanvasButton(
            id = idRegistry.understoodButtonId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.understood).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = toolbarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(mainBottomComposite),
    )

    override suspend fun start() = withContext(scope.coroutineContext) {
        sendAnalyticEvent(AnalyticEvent.VIEW)
        updateUiData()
        dataStore.saveCvvOnboardingShown()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnUnderstood(entity: UiEntityOfCanvasButton<*>) {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.understoodButtonId)
        if (isAvailable.not()) return

        availabilityRegistry.setAvailabilityForAll(false)
        sendClickEvent(AnalyticsConstant.UNDERSTOOD)
        receiveEvent(NavigationIntention.BACK)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private fun sendAnalyticEvent(event: AnalyticEvent, data: Map<String, Any> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        analyticModel.sendEvent(eventData)
    }

    private fun sendClickEvent(label: String) {
        val data: Map<String, Any> = mapOf(AnalyticsConstant.EVENT_LABEL to label)
        sendAnalyticEvent(AnalyticEvent.CLICK, data)
    }
}
