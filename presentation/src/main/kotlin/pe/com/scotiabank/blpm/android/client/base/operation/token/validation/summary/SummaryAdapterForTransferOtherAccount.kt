package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.model.ConfirmationModel
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.model.TransferSummaryModel
import pe.com.scotiabank.blpm.android.client.model.VoucherBodyModel
import pe.com.scotiabank.blpm.android.client.transfer.base.BaseTransferMapper
import pe.com.scotiabank.blpm.android.client.transfer.otheraccount.TransferOtherAccountsModelDataMapper
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.ConfirmationEntity
import java.lang.ref.WeakReference

class SummaryAdapterForTransferOtherAccount(
    private val weakResources: WeakReference<Resources?>,
    private val originProduct: ProductModel,
): SummaryAdapter<ConfirmationEntity, TransferSummaryModel> {

    override fun adapt(responseEntity: ConfirmationEntity): TransferSummaryModel {

        val confirmation: ConfirmationModel = TransferOtherAccountsModelDataMapper.transformConfirmation(
            responseEntity,
        )

        val voucherBodies: List<VoucherBodyModel> = TransferOtherAccountsModelDataMapper.transformOtherAccountConfirmation(
            confirmation,
            originProduct,
            weakResources.get(),
        )

        return BaseTransferMapper.transformBaseConfirmationModel(
            confirmation,
            weakResources.get()?.getString(R.string.title_transfer_other_accouns).orEmpty(),
            voucherBodies,
            Constant.OTHER_ACCOUNTS,
        )
    }
}
