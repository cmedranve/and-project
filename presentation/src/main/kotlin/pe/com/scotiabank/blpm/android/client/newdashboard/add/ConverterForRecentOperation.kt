package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.date.DateFormatter
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.model.RecentTransactionModel
import java.lang.ref.WeakReference

class ConverterForRecentOperation(
    private val weakResources: WeakReference<Resources?>,
    private val dateFormatter: DateFormatter,
) {

    fun toFrequentOperation(
        recentOperation: RecentTransactionModel,
    ): FrequentOperationModel = FrequentOperationModel().apply {
        id = recentOperation.id
        title = recentOperation.title
        type = recentOperation.type
        isSuccess = true
        institutionId = recentOperation.institutionId
        institutionName = recentOperation.institutionName
        serviceCode = recentOperation.serviceCode
        paymentCode = recentOperation.paymentCode
        zonal = recentOperation.zonal
        subTitle1 = createTextForSubTitle1(recentOperation)
        subTitle2 = createTextForSubTitle2(recentOperation)
        amount = recentOperation.amount ?: 0.0
    }

    private fun createTextForSubTitle1(
        recentOperation: RecentTransactionModel,
    ): String = weakResources.get()
        ?.getString(
            R.string.my_list_add_checkable_payment_title,
            recentOperation.serviceLabelPaymentCode,
            recentOperation.paymentCode.orEmpty()
        ).orEmpty()

    private fun createTextForSubTitle2(recentOperation: RecentTransactionModel): String {

        val dateFormatted: String = recentOperation.paymentDate
            ?.let(dateFormatter::format)
            ?: return Constant.EMPTY_STRING

        return weakResources.get()
            ?.getString(R.string.my_list_paid_on, dateFormatted)
            .orEmpty()
    }
}
