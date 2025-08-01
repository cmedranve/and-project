package pe.com.scotiabank.blpm.android.client.newdashboard.businessdashboard

import androidx.lifecycle.viewModelScope
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent
import pe.com.scotiabank.blpm.android.client.scotiapay.UserDataUpdater

class BusinessDashboardViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val appModel: AppModel,
    private val userDataUpdater: UserDataUpdater,
) : NewBaseViewModel(),
    DispatcherProvider by dispatcherProvider
{

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    fun setUp(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
        userDataUpdater.attemptToUpdate()
    }

    fun logout() = viewModelScope.launch {
        setLoadingV2(true)
        @Suppress("UNUSED_VARIABLE")
        val isSuccessfullyLoggedOut: Boolean = appModel.receive(SessionEvent.ENDING)
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(SessionEvent.LOGGED_OUT)
    }
}