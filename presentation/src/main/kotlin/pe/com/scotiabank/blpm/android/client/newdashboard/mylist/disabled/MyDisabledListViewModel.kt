package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.disabled

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.Intention
import pe.com.scotiabank.blpm.android.client.base.ViewModelWithSheetDialog
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling

class MyDisabledListViewModel(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopCompositeForDisabled.Factory,
    override val id: Long = randomLong(),
    private val mutableLiveHolder: MutableLiveHolder = MutableLiveHolder(),
    recycling: Recycling = StatefulRecycling(),
): NewBaseViewModel(),
    ViewModelWithSheetDialog,
    LiveHolder by mutableLiveHolder,
    Recycling by recycling
{

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            Intention::class,
            InstancePredicate(::filterInMyListTabClicked),
            InstanceHandler(::onMyListTabClicked)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = false,
            iconRes = pe.com.scotiabank.blpm.android.ui.R.drawable.ic_logo,
        )

    private val mainTopComposite: MainTopCompositeForDisabled = factoryOfMainTopComposite.create(selfReceiver)

    private var _liveCompoundsOfSheetDialog: MutableLiveData<List<UiCompound<*>>> = MutableLiveData()
    override val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
        get() = _liveCompoundsOfSheetDialog

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    override fun receiveEvent(event: Any): Boolean = selfReceiver.receive(event)

    override fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
    }

    private fun filterInMyListTabClicked(
        intention: Intention,
    ): Boolean = Intention.NOTIFY_CLICK_ON_MY_LIST_TAB == intention

    @Suppress("UNUSED_PARAMETER")
    private fun onMyListTabClicked(intention: Intention) = viewModelScope.launch {
        mainTopComposite.currentState = UiState.DISABLED
        putUiDataLaunchedByCoroutineScope()
    }

    private suspend fun putUiDataLaunchedByCoroutineScope() {
        toolbarComposite.recomposeItselfIfNeeded()
        mainTopComposite.recomposeItselfIfNeeded()

        mutableLiveHolder.notifyAppBarAndMain(toolbarComposite.compounds, mainTopComposite.compounds)
    }
}
