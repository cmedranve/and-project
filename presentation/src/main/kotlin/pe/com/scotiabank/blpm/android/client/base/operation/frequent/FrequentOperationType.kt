package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import androidx.arch.core.util.Function
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.findTemplateForPayments
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.findTemplateForTransfers
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate

enum class FrequentOperationType (
    val id: Long,
    val optionTemplateFinder: Function<FeatureTemplate, OptionTemplate>,
    val typeFromNetworkCall: String,
    val displayText: String,
    val analyticValueOfDisplayText: String,
    val actionText: String,
    val analyticValueOfActionText: String,
    val retryId: Long,
) {

    TRANSFER(
        id = randomLong(),
        optionTemplateFinder = Function(::findTemplateForTransfers),
        typeFromNetworkCall = "TRANSFER",
        displayText = "Transferencias",
        analyticValueOfDisplayText = "transferencias",
        actionText = "Transferir",
        analyticValueOfActionText = "transferir",
        retryId = randomLong(),
    ),
    PAYMENT(
        id = randomLong(),
        optionTemplateFinder = Function(::findTemplateForPayments),
        typeFromNetworkCall = "PAYMENT",
        displayText = "Pagos",
        analyticValueOfDisplayText = "pagos",
        actionText = "Pagar",
        analyticValueOfActionText = "pagar",
        retryId = randomLong(),
    );
}
