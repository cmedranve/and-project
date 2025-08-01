package pe.com.scotiabank.blpm.android.client.newdashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.com.scotiabank.blpm.android.client.app.AppModel
import javax.inject.Inject

class PersonalDashboardViewModelFactory @Inject constructor(
    private val appModel: AppModel
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalDashboardViewModel::class.java)) {
            return PersonalDashboardViewModel(appModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }
}