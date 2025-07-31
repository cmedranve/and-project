package pe.com.scotiabank.blpm.android.client.base.verification

enum class TransactionType(val typeForNetworkCall: String) {
    TRANSFER_CONTACT("TRANSFER_CONTACT"),
    CVV("CVV"),
    SETTINGS("SETTINGS"),
}
