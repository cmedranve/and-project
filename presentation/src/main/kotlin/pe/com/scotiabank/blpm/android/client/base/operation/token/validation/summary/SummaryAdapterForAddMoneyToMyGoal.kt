package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.model.TransferSummaryModel
import pe.com.scotiabank.blpm.android.client.model.pfm.ConfirmAddMoneyToMyGoalModel
import pe.com.scotiabank.blpm.android.client.pfm.goal.addmoney.confirmation.ConfirmationAddMoneyToMyGoalDataMapper
import pe.com.scotiabank.blpm.android.data.entity.pfm.ConfirmAddMoneyToMyGoalWrapperEntity
import java.lang.ref.WeakReference

class SummaryAdapterForAddMoneyToMyGoal(
    private val weakResources: WeakReference<Resources?>,
): SummaryAdapter<ConfirmAddMoneyToMyGoalWrapperEntity, TransferSummaryModel> {

    override fun adapt(responseEntity: ConfirmAddMoneyToMyGoalWrapperEntity): TransferSummaryModel {

        val  confirmation: ConfirmAddMoneyToMyGoalModel = ConfirmationAddMoneyToMyGoalDataMapper.transformConfirmAddMoneyToMyGoal(
            responseEntity.data
        )

        return ConfirmationAddMoneyToMyGoalDataMapper.transformConfirmationAddMoneyToMyGoalModel(
            confirmation,
            weakResources.get(),
        )
    }
}
