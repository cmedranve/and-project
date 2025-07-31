package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel

interface SummaryAdapter<R: Any, S: BaseSummaryModel> {

    fun adapt(responseEntity: R): S
}
