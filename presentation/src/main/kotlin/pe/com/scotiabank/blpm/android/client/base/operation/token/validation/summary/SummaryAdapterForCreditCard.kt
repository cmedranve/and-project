package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.Context
import pe.com.scotiabank.blpm.android.client.model.CreditCardModel
import pe.com.scotiabank.blpm.android.client.model.CreditCardSummaryModel
import pe.com.scotiabank.blpm.android.client.payment.creditcard.CreditCardModelDataMapper
import pe.com.scotiabank.blpm.android.data.entity.CreditCardConfirmationEntity
import java.lang.ref.WeakReference

class SummaryAdapterForCreditCard(
    private val weakAppContext: WeakReference<Context?>,
    private val creditCard: CreditCardModel,
): SummaryAdapter<CreditCardConfirmationEntity, CreditCardSummaryModel> {

    override fun adapt(
        responseEntity: CreditCardConfirmationEntity,
    ): CreditCardSummaryModel = CreditCardModelDataMapper.transformBaseSummaryModel(
        weakAppContext.get(),
        responseEntity,
        creditCard.paymentType,
    )
}
