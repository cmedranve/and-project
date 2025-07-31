package pe.com.scotiabank.blpm.android.client.base.operation.completion

import pe.com.scotiabank.blpm.android.client.base.operation.confirmation.carrier.CarrierFromConfirmationConsumer

class DataForOperationCompletion(
    val acceptHeader: String,
    val authTracking: String,
    val transactionId: String,
    val isValidatedWithPush: Boolean,
    val carrierFromConsumer: CarrierFromConfirmationConsumer,
    val analyticData: OperationAnalyticData,
)
