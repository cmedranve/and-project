package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.res.Resources
import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import java.lang.ref.WeakReference

class TextCollectorForSuccessState(
    paddingEntity: UiEntityOfPadding,
    private val weakResources: WeakReference<Resources?>,
): CollectorOfOneColumnText {

    private val paddingEntity = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
    )

    override fun collect(): List<UiEntityOfOneColumnText> {

        val titleEntity = UiEntityOfOneColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn = createEntityOfTitleText(),
        )

        return listOf(titleEntity)
    }

    private fun createEntityOfTitleText(): UiEntityOfText {
        val text: CharSequence = weakResources.get()?.getString(R.string.list_recent_payment_title).orEmpty()
        return UiEntityOfText(com.scotiabank.canvascore.R.style.canvascore_style_headline_18, Gravity.START, text)
    }
}