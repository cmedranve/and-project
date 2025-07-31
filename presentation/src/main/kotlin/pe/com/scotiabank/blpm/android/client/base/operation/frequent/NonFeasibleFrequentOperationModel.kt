package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel

class NonFeasibleFrequentOperationModel<R: Any, S: BaseSummaryModel>: FrequentOperationModel<R, S> {

    override suspend fun verify(responseEntity: R, summary: S): Boolean = false
}
