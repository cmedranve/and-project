package pe.com.scotiabank.blpm.android.client.base.approuting

import android.net.Uri
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AppRoutingFactory
import pe.com.scotiabank.blpm.android.client.util.Constant

class AppRoutingAnalyticsModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticsFactory: AppRoutingFactory
) {

    fun sendEvent(uriOfEmbeddedUrl: Uri) {
        val utmSource: String = uriOfEmbeddedUrl.getQueryParameter(UTM_SOURCE) ?: getUtmValueFromEmbeddedUrl(uriOfEmbeddedUrl, UTM_SOURCE)
        val utmCampaign: String = uriOfEmbeddedUrl.getQueryParameter(UTM_CAMPAIGN) ?: getUtmValueFromEmbeddedUrl(uriOfEmbeddedUrl, UTM_CAMPAIGN)
        val utmMedium: String = uriOfEmbeddedUrl.getQueryParameter(UTM_MEDIUM) ?: getUtmValueFromEmbeddedUrl(uriOfEmbeddedUrl, UTM_MEDIUM)

        sendEventOfCampaignDetail(utmSource, utmCampaign, utmMedium)
        sendEventOfAppOpen(utmSource, utmCampaign, utmMedium)
    }

    private fun getUtmValueFromEmbeddedUrl(uri: Uri, utmParameter: String): String {
        val embeddedUrl: String = uri.getQueryParameter(PARAMETER_URL) ?: return getUtmDefaultValue(utmParameter)
        val queryFromUrl: String? = Uri.parse(embeddedUrl).encodedQuery
        if (queryFromUrl.isNullOrEmpty()) { return getUtmDefaultValue(utmParameter) }
        return getQueryStringValue(queryFromUrl)
    }

    private fun getUtmDefaultValue(utmParameter: String): String = when(utmParameter) {
        UTM_SOURCE -> DEFAULT_UTM_SOURCE
        UTM_MEDIUM -> DEFAULT_UTM_MEDIUM
        UTM_CAMPAIGN -> DEFAULT_UTM_CAMPAIGN
        else -> Constant.EMPTY_STRING
    }

    private fun getQueryStringValue(query: String): String {
        val startIndexQueryValue: Int = query.indexOf(QUERY_ASSIGNER) + 1
        return query.substring(startIndexQueryValue)
    }

    private fun sendEventOfCampaignDetail(
        utmSource: String,
        utmCampaign: String,
        utmMedium: String
    ) {
        val event: AnalyticsEvent = analyticsFactory.createCampaignDetailEvent(utmSource, utmCampaign, utmMedium)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun sendEventOfAppOpen(
        utmSource: String,
        utmCampaign: String,
        utmMedium: String
    ) {
        val event: AnalyticsEvent = analyticsFactory.createAppOpenEvent(utmSource, utmCampaign, utmMedium)
        analyticsDataGateway.sendEventV2(event)
    }

    companion object {
        private const val UTM_SOURCE = "utm_source"
        private const val UTM_CAMPAIGN = "utm_campaign"
        private const val UTM_MEDIUM = "utm_medium"
        private const val DEFAULT_UTM_SOURCE = "push"
        private const val DEFAULT_UTM_CAMPAIGN = "sin-campana"
        private const val DEFAULT_UTM_MEDIUM = "push"
        private const val PARAMETER_URL = "url"
        private const val QUERY_ASSIGNER = "="
    }
}
