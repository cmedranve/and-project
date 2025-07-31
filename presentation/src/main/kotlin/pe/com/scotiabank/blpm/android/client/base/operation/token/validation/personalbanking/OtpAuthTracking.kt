package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class OtpAuthTracking(val authTracking: String, val option: String) {

    SMS(authTracking = Constant.AUTH_TRACKING_SMS, option = Constant.PHONE_OTP),
    EMAIL(authTracking = Constant.AUTH_TRACKING_EMAIL, option = Constant.EMAIL_OTP),
    NONE(authTracking = Constant.EMPTY_STRING, option = Constant.EMPTY_STRING);

    companion object {

        @JvmStatic
        fun identifyBy(authTracking: String?): OtpAuthTracking = when (authTracking?.uppercase()) {
            Constant.PHONE_OTP -> SMS
            Constant.EMAIL_OTP -> EMAIL
            else -> NONE
        }
    }
}
