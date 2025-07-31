package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.PaymentModelDataMapper
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.widget.keypad.KeypadJoy
import pe.com.scotiabank.blpm.android.data.entity.EntitlementEntity
import pe.com.scotiabank.blpm.android.data.entity.PaymentConfirmationEntity

class FrequentPaymentModel(
    private val delegate: FrequentOperationModel<PaymentConfirmationEntity, PaymentSummaryModel>,
) : FrequentOperationModel<PaymentConfirmationEntity, PaymentSummaryModel> {

    override suspend fun verify(
        responseEntity: PaymentConfirmationEntity,
        summary: PaymentSummaryModel,
    ): Boolean {

        val isAllowedAddingToMyList: Boolean = isAllowedAddingToMyList(responseEntity.entitlements)

        if (isAllowedAddingToMyList) {

            summary.keypadType = if (summary.summaryModels.size == 1) KeypadJoy.KEYPAD_JOY_6 else KeypadJoy.KEYPAD_JOY_1
            return delegate.verify(responseEntity, summary)
        }

        summary.keypadType = KeypadJoy.KEYPAD_JOY_3
        return true
    }

    private fun isAllowedAddingToMyList(entitlements: List<EntitlementEntity?>?): Boolean {
        val entitlementsByType: Map<String?, String?> = PaymentModelDataMapper.getEntitlementMap(
            entitlements,
        )
        val isAllowed: Boolean = entitlementsByType[Constant.ALLOW_ADD_PAYMENT_TO_MY_LIST]
            ?.toBooleanStrictOrNull()
            ?: return false
        if (isAllowed.not()) return false
        return entitlementsByType.containsKey(Constant.RECEIPTS_PAYMENT_TYPE_SUNAT_ONE_TIME).not()
    }
}
