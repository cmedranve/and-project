package pe.com.scotiabank.blpm.android.client.base.operation.completion

import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationInformation
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant

class OperationAnalyticData(
    val information: ValidationInformation,
    val processType: String,
    val company: String = AnalyticsConstant.HYPHEN_STRING,
    val typeOfDocument: String = AnalyticsConstant.HYPHEN_STRING,
    val typeOfOriginProduct: String = AnalyticsConstant.HYPHEN_STRING,
    val originCurrency: String = AnalyticsConstant.HYPHEN_STRING,
    val typeOfDestinationProduct: String = AnalyticsConstant.HYPHEN_STRING,
    val destinationCurrency: String = AnalyticsConstant.HYPHEN_STRING,
    val amountText: String = AnalyticsConstant.HYPHEN_STRING,
    val movementType: String = AnalyticsConstant.HYPHEN_STRING,
    val status: String = AnalyticsConstant.HYPHEN_STRING,
    val paymentType: String = AnalyticsConstant.HYPHEN_STRING,
    val questionFive: String = AnalyticsConstant.HYPHEN_STRING,
    val numberOfElements: Int = ZERO_ELEMENTS,
    val installments: String = AnalyticsConstant.NOT_INTALLMENTS,
    val installmentsNumber: Int = ZERO_ELEMENTS,
) {

    companion object {

        private const val ZERO_ELEMENTS: Int = 0
    }
}
