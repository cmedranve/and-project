package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

enum class ReloadType(val value: String, val analyticsValue: String) {
    GO_RETRY_CREDIT_CARDS("",""),
    GO_RETRY_DEBIT_CARDS("",""),
    GO_RETRY_HUB_SECTIONS("","");
}
