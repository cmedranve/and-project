package pe.com.scotiabank.blpm.android.client.cards

import br.com.hst.issuergp.data.model.ProvisionInfo

class GooglePayTokenizeWrapper(
    val pushReceiptId: String,
    val institutionCode: String,
    val provisionInfo: ProvisionInfo,
)
