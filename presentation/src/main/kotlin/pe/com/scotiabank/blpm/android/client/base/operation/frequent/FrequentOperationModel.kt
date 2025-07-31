package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel

interface FrequentOperationModel<R: Any, S: BaseSummaryModel> {

    suspend fun verify(responseEntity: R, summary: S): Boolean
}
