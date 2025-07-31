package pe.com.scotiabank.blpm.android.client.analytics

import androidx.core.util.Supplier
import com.scotiabank.sdk.analytics.AnalyticEvent
import com.scotiabank.sdk.crasherrorreporting.network.EnhancedNetworkCall
import com.scotiabank.sdk.crasherrorreporting.network.NetworkErrorProvider
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.system.Device
import pe.com.scotiabank.blpm.android.analytics.system.Network
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.normalizeText

class BaseAnalyticEvent(
    private val device: Device? = null,
    val network: Network? = null,
    private val platformSupplier: Supplier<String>,
    private val personSupplier: Supplier<String>,
    private val networkErrorProvider: NetworkErrorProvider? = null,
) {
    companion object {
        private const val NETWORK_TYPE = "tipo_conexion"
        private const val ANDROID_DEVICE_ID = "id_dispositivo"
        private const val PLATFORM_TYPE_HIT = "platform_type_hit";
        private const val PERSON_TYPE_HIT = "person_type_hit";
    }

    fun createBaseAnalyticEvent(): AnalyticEvent {
        val networkType = network?.currentInternetConnectionType
        val currentInternetConnectionType = networkType ?: Constant.HYPHEN_STRING
        val androidDeviceId = device?.androidDeviceId ?: Constant.HYPHEN_STRING

        val lastNetworkErrorCall = networkErrorProvider?.lastNetworkErrorCall
        val errorCode = getErrorCode(lastNetworkErrorCall)
        val errorMessage = getErrorMessage(lastNetworkErrorCall).normalizeText()

        return AnalyticEvent.Builder()
            .add(NETWORK_TYPE, currentInternetConnectionType)
            .add(ANDROID_DEVICE_ID, androidDeviceId)
            .add(AnalyticsBaseConstant.ERROR_CODE, errorCode)
            .add(AnalyticsBaseConstant.ERROR_MESSAGE, errorMessage)
            .add(PLATFORM_TYPE_HIT, platformSupplier.get())
            .add(PERSON_TYPE_HIT, personSupplier.get())
            .build()
    }

    private fun getErrorCode(lastNetworkErrorCall: EnhancedNetworkCall?): String {
        val metadata = lastNetworkErrorCall?.metadata
        val value = metadata?.values?.get(AnalyticsBaseConstant.ERROR_CODE)
        return value ?: Constant.HYPHEN_STRING
    }

    private fun getErrorMessage(lastNetworkErrorCall: EnhancedNetworkCall?): String {
        val metadata = lastNetworkErrorCall?.metadata
        val value = metadata?.values?.get(AnalyticsBaseConstant.ERROR_MESSAGE)
        return value ?: Constant.HYPHEN_STRING
    }

}
