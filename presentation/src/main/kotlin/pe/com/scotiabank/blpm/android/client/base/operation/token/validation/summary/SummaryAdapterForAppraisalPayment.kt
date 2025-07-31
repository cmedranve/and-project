package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.payment.appraisal.confirm.AppraisalPaymentMapper
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.PaymentConfirmationEntity
import java.lang.ref.WeakReference

class SummaryAdapterForAppraisalPayment(
    private val weakResources: WeakReference<Resources?>,
): SummaryAdapter<PaymentConfirmationEntity, PaymentSummaryModel> {

    private var institutionId: String = Constant.EMPTY_STRING
    private var serviceCode: String = Constant.EMPTY_STRING
    private var zonalId: String = Constant.EMPTY_STRING

    override fun adapt(responseEntity: PaymentConfirmationEntity): PaymentSummaryModel {

        val mapper = AppraisalPaymentMapper(weakResources)

        val summary: PaymentSummaryModel = mapper.toPaymentSummary(responseEntity)

        summary.institutionId = institutionId
        summary.serviceCode = serviceCode
        summary.zonalId = zonalId

        return summary
    }
}
