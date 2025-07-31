package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.data.entity.FrequentPaymentEntity

interface FrequentFactory<S: BaseSummaryModel> {

    fun create(summary: S): FrequentPaymentEntity
}
