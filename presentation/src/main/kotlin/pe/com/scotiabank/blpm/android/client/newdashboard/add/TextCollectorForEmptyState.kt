package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.res.Resources
import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import java.lang.ref.WeakReference

class TextCollectorForEmptyState(
    paddingEntity: UiEntityOfPadding,
    private val weakResources: WeakReference<Resources?>,
): CollectorOfOneColumnText {

    private val paddingEntity = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
    )

    override fun collect(): List<UiEntityOfOneColumnText> {

        val titleEntity = UiEntityOfOneColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn = createEntityOfTitleText(),
        )
        val descriptionEntity = UiEntityOfOneColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn = createEntityOfDescriptionText(),
        )

        return listOf(titleEntity, descriptionEntity)
    }

    private fun createEntityOfTitleText(): UiEntityOfText {
        val text: CharSequence = weakResources.get()?.getString(R.string.list_recent_transactions_empty_state_title).orEmpty()
        return UiEntityOfText(com.scotiabank.canvascore.R.style.canvascore_style_headline_small_black, Gravity.CENTER, text)
    }

    private fun createEntityOfDescriptionText(): UiEntityOfText {
        val text: CharSequence = weakResources.get()?.getString(R.string.list_recent_payments_empty_state_sub_title).orEmpty()
        return UiEntityOfText(com.scotiabank.canvascore.R.style.canvascore_style_body2, Gravity.CENTER, text)
    }
}