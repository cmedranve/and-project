package pe.com.scotiabank.blpm.android.client.app

class PushOtpFlowChecker(private val appModel: AppModel) {

    val isPushOtpEnabled: Boolean
        get() = appModel.profile.isPushOtpEnabled
}
