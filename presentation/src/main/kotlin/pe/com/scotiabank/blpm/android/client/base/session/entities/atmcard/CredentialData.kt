package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

class CredentialData(
    val actionRequired: ActionRequired,
    val cardNumber: CharSequence,
    val expiryDate: CharSequence,
    val code: CharSequence,
)
