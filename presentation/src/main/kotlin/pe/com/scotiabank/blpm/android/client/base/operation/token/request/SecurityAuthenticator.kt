package pe.com.scotiabank.blpm.android.client.base.operation.token.request

import android.content.Context
import com.scotiabank.proofofkey.auth.utilities.BiometricUtils
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.biometric.getDeviceId
import pe.com.scotiabank.blpm.android.client.util.biometric.hasEnrolledCamsId
import pe.com.scotiabank.blpm.android.client.util.biometric.hasTxEnabled
import java.lang.ref.WeakReference

class SecurityAuthenticator(
    private val appModel: AppModel,
    private val weakAppContext: WeakReference<Context?>,
) {

    private val camsId: String
        get() = appModel.profile.id

    private val hasAvailableBiometric: Boolean
        get() = weakAppContext.get()
            ?.let(BiometricUtils::hasAvailableBiometric)
            ?: false

    private val hasEnrolledCamsId: Boolean
        get() = weakAppContext.get()
            ?.hasEnrolledCamsId(camsId)
            ?: false

    private val hasTxEnabled: Boolean
        get() = weakAppContext.get()
            ?.hasTxEnabled()
            ?: false

    val type: String
        get() {
            if (hasAvailableBiometric.not()) return Constant.SECURITY_AUTH_TYPE_TOKEN
            if (hasEnrolledCamsId.not()) return Constant.SECURITY_AUTH_TYPE_TOKEN
            if (hasTxEnabled.not()) return Constant.SECURITY_AUTH_TYPE_TOKEN

            return Constant.SECURITY_AUTH_TYPE_BIOMETRIC
        }

    val deviceId: String
        get() {
            if (hasAvailableBiometric.not()) return Constant.EMPTY_STRING
            if (hasEnrolledCamsId.not()) return Constant.EMPTY_STRING
            if (hasTxEnabled.not()) return Constant.EMPTY_STRING

            return weakAppContext.get()
                ?.let(::getDeviceId)
                .orEmpty()
        }
}
