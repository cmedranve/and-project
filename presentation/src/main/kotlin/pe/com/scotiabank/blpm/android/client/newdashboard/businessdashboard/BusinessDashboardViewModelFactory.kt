package pe.com.scotiabank.blpm.android.client.newdashboard.businessdashboard

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fasterxml.jackson.databind.ObjectMapper
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.scotiapay.UserDataUpdater
import pe.com.scotiabank.blpm.android.client.host.shared.DataStore
import pe.com.scotiabank.blpm.android.client.host.user.UserDao
import javax.inject.Inject

class BusinessDashboardViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val appModel: AppModel,
    objectMapper: ObjectMapper,
    defaultSharedPreferences: SharedPreferences,
): ViewModelProvider.Factory {

    private val userDao: UserDao = DataStore(objectMapper, defaultSharedPreferences)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessDashboardViewModel::class.java)) {
            return createViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    private fun createViewModel(): BusinessDashboardViewModel {

        val userDataUpdater = UserDataUpdater(
            appModel = appModel,
            userDao = userDao,
        )

        return BusinessDashboardViewModel(
            dispatcherProvider = dispatcherProvider,
            appModel = appModel,
            userDataUpdater = userDataUpdater,
        )
    }
}