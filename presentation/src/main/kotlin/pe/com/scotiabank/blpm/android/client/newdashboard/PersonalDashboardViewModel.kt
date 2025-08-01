package pe.com.scotiabank.blpm.android.client.newdashboard

import androidx.lifecycle.viewModelScope
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent

class PersonalDashboardViewModel(private val appModel: AppModel): NewBaseViewModel() {

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    fun setUp(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
    }

    fun logout() = viewModelScope.launch {
        setLoadingV2(true)
        @Suppress("UNUSED_VARIABLE")
        val isSuccessfullyLoggedOut: Boolean = appModel.receive(SessionEvent.ENDING)
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(SessionEvent.LOGGED_OUT)
    }
}