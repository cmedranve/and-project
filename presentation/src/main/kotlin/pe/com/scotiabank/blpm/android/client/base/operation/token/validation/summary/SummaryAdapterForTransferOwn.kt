package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.model.ConfirmationModel
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.model.TransferSummaryModel
import pe.com.scotiabank.blpm.android.client.model.VoucherBodyModel
import pe.com.scotiabank.blpm.android.client.transfer.base.BaseTransferMapper
import pe.com.scotiabank.blpm.android.client.transfer.base.BaseTransferModelDataMapper
import pe.com.scotiabank.blpm.android.client.transfer.own.TransferOwnAccountsModelDataMapper
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.ConfirmationEntity
import java.lang.ref.WeakReference

class SummaryAdapterForTransferOwn(
    private val weakResources: WeakReference<Resources?>,
    private val originProduct: ProductModel,
    private val destinationProduct: ProductModel,
): SummaryAdapter<ConfirmationEntity, TransferSummaryModel> {

    override fun adapt(responseEntity: ConfirmationEntity): TransferSummaryModel {

        val confirmation: ConfirmationModel = BaseTransferModelDataMapper.transformConfirmation(
            responseEntity,
        )

        val voucherBodies: List<VoucherBodyModel> = TransferOwnAccountsModelDataMapper.transformOwnAccountConfirmation(
            confirmation,
            originProduct,
            destinationProduct,
            weakResources.get(),
        )

        return BaseTransferMapper.transformBaseConfirmationModel(
            confirmation,
            weakResources.get()?.getString(R.string.title_transfer_own_transfer).orEmpty(),
            voucherBodies,
            Constant.OWN_ACCOUNTS,
        )
    }
}
