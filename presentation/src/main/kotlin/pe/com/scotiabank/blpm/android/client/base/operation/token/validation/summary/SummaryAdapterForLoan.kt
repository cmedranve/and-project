package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.payment.loan.LoanModelMapper
import pe.com.scotiabank.blpm.android.data.entity.LoanConfirmationEntity
import java.lang.ref.WeakReference

class SummaryAdapterForLoan(
    private val weakResources: WeakReference<Resources?>,
): SummaryAdapter<LoanConfirmationEntity, BaseSummaryModel> {

    override fun adapt(
        responseEntity: LoanConfirmationEntity,
    ): BaseSummaryModel = LoanModelMapper.transformLoanConfirmationEntity(
        responseEntity,
        weakResources.get(),
    )
}
