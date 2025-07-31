package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.PaymentModelDataMapper
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.PaymentConfirmationEntity
import pe.com.scotiabank.blpm.android.data.entity.PaymentEntity
import pe.com.scotiabank.blpm.android.data.exception.GenericException
import java.lang.ref.WeakReference

class SummaryAdapterForPayment(
    private val weakResources: WeakReference<Resources?>,
    private val institutionId: String,
    private val serviceCode: String,
    private val zonalId: String,
): SummaryAdapter<PaymentConfirmationEntity, PaymentSummaryModel> {

    override fun adapt(responseEntity: PaymentConfirmationEntity): PaymentSummaryModel {

        val paymentEntities: List<PaymentEntity> = responseEntity.paymentEntities
        val paymentEntity: PaymentEntity? = paymentEntities.firstOrNull(::isFailedStatus)

        if (paymentEntity != null) {
            throw GenericException(
                title = paymentEntity.errorTitle.orEmpty(),
                errorCode = paymentEntity.errorCode.orEmpty(),
                message = paymentEntity.errorMessage.orEmpty(),
            )
        }

        val summary: PaymentSummaryModel = PaymentModelDataMapper.transformPaymentConfirmationToModel(
            responseEntity,
            weakResources.get(),
        )

        summary.institutionId = institutionId
        summary.serviceCode = serviceCode
        summary.zonalId = zonalId

        return summary
    }

    private fun isFailedStatus(entity: PaymentEntity): Boolean = Constant.FAILED.equals(entity.status)
}
