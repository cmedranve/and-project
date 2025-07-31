package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.model.RechargeConfirmationModel
import pe.com.scotiabank.blpm.android.client.model.TransferSummaryModel
import pe.com.scotiabank.blpm.android.client.recharge.RechargeModelDataMapper
import pe.com.scotiabank.blpm.android.data.entity.RechargeConfirmationEntity
import java.lang.ref.WeakReference

class SummaryAdapterForRecharge(
    private val weakResources: WeakReference<Resources?>,
): SummaryAdapter<RechargeConfirmationEntity, TransferSummaryModel> {

    override fun adapt(
        responseEntity: RechargeConfirmationEntity,
    ): TransferSummaryModel {

        val confirmation: RechargeConfirmationModel = RechargeModelDataMapper.transformRechargeConfirmationModel(
            responseEntity,
        )

        return RechargeModelDataMapper.transformRechargeConfirmationTransferModel(
            weakResources.get()?.getString(R.string.title_recharges_summary).orEmpty(),
            confirmation,
            weakResources.get(),
        )
    }
}
