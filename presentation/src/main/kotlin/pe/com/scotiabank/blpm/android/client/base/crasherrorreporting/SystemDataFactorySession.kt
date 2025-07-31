package pe.com.scotiabank.blpm.android.client.base.crasherrorreporting

import androidx.core.util.Supplier
import com.scotiabank.sdk.crasherrorreporting.network.EnhancedNetworkCall
import com.scotiabank.sdk.crasherrorreporting.network.NetworkErrorProvider
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory
import pe.com.scotiabank.blpm.android.analytics.system.Device
import pe.com.scotiabank.blpm.android.analytics.system.Network
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsUtil
import pe.com.scotiabank.blpm.android.client.util.Constant

class SystemDataFactorySession(
    private val device: Device,
    private val platformSupplier: Supplier<String>,
    private val personSupplier: Supplier<String>,
    private val network: Network,
    private val networkErrorProvider: NetworkErrorProvider
) : SystemDataFactory {

    override fun createGenericAnalyticsEvent(): AnalyticsEvent = AnalyticsEvent.Builder()
        .add(SystemDataFactory.NETWORK_TYPE, network.currentInternetConnectionType)
        .add(SystemDataFactory.ANDROID_DEVICE_ID, device.androidDeviceId)
        .add(PLATFORM_TYPE_HIT, platformSupplier.get())
        .add(PERSON_TYPE_HIT, personSupplier.get())
        .apply {
            networkErrorProvider.lastNetworkErrorCall?.let { error ->
                val errorCode = getErrorCode(error)
                val errorMessage = getErrorMessage(error)
                add(AnalyticsBaseConstant.ERROR_CODE, errorCode)
                addNormalizeText(
                    AnalyticsBaseConstant.ERROR_MESSAGE,
                    AnalyticsUtil.getMessageMinLength(errorMessage)
                )
            }
        }
        .build()

    private fun getErrorCode(
        lastNetworkErrorCall: EnhancedNetworkCall,
    ): String = lastNetworkErrorCall
        .metadata
        .values[AnalyticsBaseConstant.ERROR_CODE] ?: Constant.HYPHEN_STRING

    private fun getErrorMessage(
        lastNetworkErrorCall: EnhancedNetworkCall,
    ): String = lastNetworkErrorCall
        .metadata
        .values[AnalyticsBaseConstant.ERROR_MESSAGE] ?: Constant.HYPHEN_STRING

    companion object {
        private const val PLATFORM_TYPE_HIT = "platform_type_hit"
        private const val PERSON_TYPE_HIT = "person_type_hit"
    }
}
