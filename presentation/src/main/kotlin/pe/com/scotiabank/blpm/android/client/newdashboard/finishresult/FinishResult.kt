package pe.com.scotiabank.blpm.android.client.newdashboard.finishresult

class FinishResult {

    private var _isLoop2PayGoToHome: Boolean = false
    val isLoop2PayGoToHome: Boolean
        get() {
            val value = _isLoop2PayGoToHome
            _isLoop2PayGoToHome = false
            return value
        }

    private var _isCardSettingsSelected: Boolean = false
    val isCardSettingsSelected: Boolean
        get() {
            val value = _isCardSettingsSelected
            _isCardSettingsSelected = false
            return value
        }

    fun onQRGeneralAccessFinished(isCardSettingsSelected: Boolean, isGoToHome: Boolean) {
        _isCardSettingsSelected = isCardSettingsSelected
        _isLoop2PayGoToHome = isGoToHome
    }
}
