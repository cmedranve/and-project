package pe.com.scotiabank.blpm.android.client.atmcardhub.shared

import androidx.core.util.Consumer
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

class DataForPushOtpVerification(

    val transactionId: String,
    val analyticConsumer: Consumer<AnalyticEventData<*>>,
    val analyticAdditionalData: Any,
)
