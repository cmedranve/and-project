package pe.com.scotiabank.blpm.android.client.analytics

import com.scotiabank.sdk.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.util.analytics.AnalyticsBaseConstant

const val previousSection = AnalyticsBaseConstant.PREVIOUS_SECTION

fun AnalyticEvent?.getPreviousSection() = this?.find { previousSection == it.key }?.value
