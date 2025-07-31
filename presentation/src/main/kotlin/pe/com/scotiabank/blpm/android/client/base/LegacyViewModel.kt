package pe.com.scotiabank.blpm.android.client.base

import androidx.lifecycle.LiveData
import pe.com.scotiabank.blpm.android.client.util.EventWrapper
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel

interface LegacyViewModel: PortableViewModel {

    fun getLoadingV2(): LiveData<EventWrapper<Boolean>>

    fun getErrorMessageV2(): LiveData<EventWrapper<BaseAppearErrorMessage>>
}
