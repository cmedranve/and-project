package pe.com.scotiabank.blpm.android.client.base.analytics

class AnalyticLegacyEventData<T>(
    val legacyEvent: AnalyticEventData<T>,
    val legacyData: Map<String, Any?>
)
