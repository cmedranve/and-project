package pe.com.scotiabank.blpm.android.client.newdashboard.add

import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel

class CarrierOfOperationListAdded(
    val operationsAdded: List<FrequentOperationModel>,
    val type: FrequentOperationType,
)