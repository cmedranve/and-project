package pe.com.scotiabank.blpm.android.client.analytics

@Deprecated(
    message = "It is recommended to use the new version supported with Map instead of vararg for the data argument.",
    replaceWith = ReplaceWith("AnalyticEventData", "pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData"),
    level = DeprecationLevel.WARNING
)
class AnalyticEventData<T>(val event: T, vararg val data: Any?)
