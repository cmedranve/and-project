package pe.com.scotiabank.blpm.android.client.base.receipt

class ReceiptAnalyticsData(
    var thisInSummary: Boolean,
    val description: String?,
    val elementsNumber: String?,
    val institution: String?,
    val amount: String?,
    val currency: String?,
    val installmentsNumber: String?,
    val authenticationChannel: String?,
    val typePay: String?,
    val typeOriginAccount: String?,
    val typeService: String?,
    val status: String?,
)
