package pe.com.scotiabank.blpm.android.client.newdashboard.finishresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel

class FinishResultViewModel : NewBaseViewModel() {

    private val finishResult = FinishResult()

    private val _finishResultLiveData: MutableLiveData<FinishResult> = MutableLiveData()
    val finishResultLiveData: LiveData<FinishResult>
        get() = _finishResultLiveData

    fun onQRGeneralAccessFinished(isCardSettingsSelected: Boolean, isGoToHome: Boolean) {
        finishResult.onQRGeneralAccessFinished(isCardSettingsSelected, isGoToHome)
        _finishResultLiveData.postValue(finishResult)
    }
}
