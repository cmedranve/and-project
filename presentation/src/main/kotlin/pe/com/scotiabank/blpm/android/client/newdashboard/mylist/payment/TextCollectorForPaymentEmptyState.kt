package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import android.content.res.Resources
import android.view.Gravity
import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import java.lang.ref.WeakReference

class TextCollectorForPaymentEmptyState(
    paddingEntity: UiEntityOfPadding,
    private val weakResources: WeakReference<Resources?>,
): CollectorOfOneColumnText {

    private val paddingEntityForTop = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_84,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
    )

    private val paddingEntityForBottom = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
    )

    override fun collect(): List<UiEntityOfOneColumnText> {

        val descriptionEntityForTop = UiEntityOfOneColumnText(
            paddingEntity = paddingEntityForTop,
            entityOfColumn = createEntityOfDescriptionText(R.string.my_list_empty_payment_description_1),
        )

        val descriptionEntityForBottom = UiEntityOfOneColumnText(
            paddingEntity = paddingEntityForBottom,
            entityOfColumn = createEntityOfDescriptionText(R.string.my_list_empty_payment_description_2),
        )

        return listOf(descriptionEntityForTop, descriptionEntityForBottom)
    }

    private fun createEntityOfDescriptionText(@StringRes textResId: Int): UiEntityOfText {
        val text: CharSequence = weakResources.get()?.getString(textResId).orEmpty()
        return UiEntityOfText(com.scotiabank.canvascore.R.style.canvascore_style_body2, Gravity.CENTER, text)
    }
}