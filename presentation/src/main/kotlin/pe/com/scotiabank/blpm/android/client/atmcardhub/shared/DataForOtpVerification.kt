package pe.com.scotiabank.blpm.android.client.atmcardhub.shared

class DataForOtpVerification(
    val debitOperationId: String,
    val accountType: String,
    val questionOne: String,
    val questionThree: String,
    val productType: String,
    val previousSectionDetail: String,
)
