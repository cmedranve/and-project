package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled

import android.content.res.Resources
import androidx.core.util.Supplier
import androidx.lifecycle.viewModelScope
import com.scotiabank.canvascore.buttons.CanvasButton
import com.scotiabank.enhancements.handling.*
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.analytics.factories.cards.walletnotinstalled.WalletNotInstalledFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.EVENT_LABEL
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled.analytics.AnalyticModel
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling
import java.lang.ref.WeakReference

class MainViewModel(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    private val idRegistry: IdRegistry,
    private val analyticModel: AnalyticModel,
    override val id: Long = randomLong(),
    private val mutableLiveHolder: MutableLiveHolder = MutableLiveHolder(),
    recycling: Recycling = StatefulRecycling(),
) : NewBaseViewModel(), PortableViewModel, LiveHolder by mutableLiveHolder, Recycling by recycling {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInPrimaryButtonClicked),
            InstanceHandler(::handleClickOnPrimaryButton)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInSecondaryButtonClicked),
            InstanceHandler(::handleClickOnSecondaryButton)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite.create(selfReceiver)

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create()

    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isGoingToBeVisible),
        )
        .addCanvasButton(
            id = idRegistry.findButtonPrimaryId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.download_Google_wallet).orEmpty(),
        )
        .addCanvasButton(
            id = idRegistry.findButtonSecondaryId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.at_another_time).orEmpty(),
            type = CanvasButton.SECONDARY,
        )

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    override fun receiveEvent(event: Any): Boolean = selfReceiver.receive(event)

    private fun sendAnalyticEvent(event: AnalyticEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        analyticModel.sendEvent(eventData)
    }

    override fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
        sendAnalyticEvent(AnalyticEvent.SCREEN)
        onScreenCreated()
    }

    private fun onScreenCreated() = viewModelScope.launch {
        putUiDataLaunchedByCoroutineScope()
    }

    private suspend fun putUiDataLaunchedByCoroutineScope() {
        toolbarComposite.recomposeItselfIfNeeded()
        mainTopComposite.recomposeItselfIfNeeded()
        mainBottomComposite.recomposeItselfIfNeeded()

        mutableLiveHolder.notifyAppBarAndMain(
            appBar = toolbarComposite.compounds,
            mainTop = mainTopComposite.compounds,
            mainBottom = mainBottomComposite.compounds
        )
    }

    private fun filterInPrimaryButtonClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == idRegistry.findButtonPrimaryId

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnPrimaryButton(entity: UiEntityOfCanvasButton<*>) = viewModelScope.launch {
        sendClickEvent(WalletNotInstalledFactory.DOWNLOAD_GOOGLE_WALLET)
        receiverOfViewModelEvents?.receive(Intention.DOWNLOAD_GOOGLE_WALLET)
    }

    private fun sendClickEvent(label: String) {
        val data: MutableMap<String, Any?> = mutableMapOf(EVENT_LABEL to label)
        sendAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    private fun filterInSecondaryButtonClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == idRegistry.findButtonSecondaryId

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnSecondaryButton(entity: UiEntityOfCanvasButton<*>) = viewModelScope.launch {
        sendClickEvent(WalletNotInstalledFactory.IN_ANOTHER_MOMENT)
        receiverOfViewModelEvents?.receive(Intention.CLOSE_ACTIVITY)
    }

}
