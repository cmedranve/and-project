package pe.com.scotiabank.blpm.android.client.base.crasherrorreporting

import com.scotiabank.sdk.crasherrorreporting.network.EnhancedNetworkCall
import com.scotiabank.sdk.crasherrorreporting.network.NetworkErrorProvider
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory
import pe.com.scotiabank.blpm.android.analytics.system.Device
import pe.com.scotiabank.blpm.android.analytics.system.Network
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsUtil
import pe.com.scotiabank.blpm.android.client.util.Constant

class SystemDataFactoryOutSession(
    private val device: Device,
    private var network: Network,
    private val networkErrorProvider: NetworkErrorProvider
) : SystemDataFactory {

    override fun createGenericAnalyticsEvent(): AnalyticsEvent = AnalyticsEvent.Builder()
        .add(SystemDataFactory.NETWORK_TYPE, network.currentInternetConnectionType)
        .add(SystemDataFactory.ANDROID_DEVICE_ID, device.androidDeviceId)
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
}
