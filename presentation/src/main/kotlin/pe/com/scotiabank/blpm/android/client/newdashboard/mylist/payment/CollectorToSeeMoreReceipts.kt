package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import android.content.res.Resources
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.CarrierOfFrequentOperation
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.IdentifiableTextButton
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference

class CollectorToSeeMoreReceipts(
    private val receiver: InstanceReceiver,
    private val weakResources: WeakReference<Resources?>,
) {

    private val emptyPadding: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    private val seeMoreReceipts: CharSequence by lazy {
        weakResources.get()?.getString(R.string.my_list_payment_see_more_receipts).orEmpty()
    }

    fun collect(
        operation: FrequentOperationModel,
    ): List<UiEntityOfTextButton<CarrierOfFrequentOperation>> {

        if (operation.isShowMore.not()) return emptyList()

        val carrier = CarrierOfFrequentOperation(IdentifiableTextButton.SEE_MORE_RECEIPTS, operation)
        val entity: UiEntityOfTextButton<CarrierOfFrequentOperation> = UiEntityOfTextButton(
            paddingEntity = emptyPadding,
            isEnabled = true,
            text = seeMoreReceipts,
            receiver = receiver,
            data = carrier,
            drawableEndId = com.scotiabank.canvascore.R.drawable.canvascore_icon_chevron_right_blue,
        )
        return listOf(entity)
    }
}