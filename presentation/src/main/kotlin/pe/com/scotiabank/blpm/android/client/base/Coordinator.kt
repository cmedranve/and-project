package pe.com.scotiabank.blpm.android.client.base

import androidx.lifecycle.LiveData
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfAppBarAndMain
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.EventHandler

interface Coordinator : CoordinatorRegistry, DispatcherProvider, LiveHolder, Recycling, EventHandler {

    val id: Long

    override val liveCompositeOfAppBarAndMain: LiveData<CompositeOfAppBarAndMain>

    override val liveAnchoredBottomCompounds: LiveData<List<UiCompound<*>>>

    val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
        get() = currentChild.liveCompoundsOfSheetDialog

    suspend fun start() {
        // do nothing if not required
    }

    fun receiveFromChild(event: Any)

    fun receiveFromAncestor(event: Any) {
        // do nothing if not required
    }

    suspend fun updateUiData()

    suspend fun clearCoordinator() {
        // do cleanup here
    }

    /**
     * This method will be called when this Coordinator is no longer used and will be destroyed.
     *
     *
     * It is useful when Coordinator observes some data and you need to clear this subscription to
     * prevent a leak of this Coordinator.
     */
    suspend fun onCoordinatorCleared() {
        // called after clear()
    }
}
