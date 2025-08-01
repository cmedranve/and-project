package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary

import android.content.res.Resources
import android.text.SpannableStringBuilder
import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import java.lang.ref.WeakReference

class TextCollectorForSummaryEmptyState(
    paddingEntity: UiEntityOfPadding,
    private val appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
): CollectorOfOneColumnText {

    private val paddingEntity = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
    )

    private val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(Constant.EMPTY_STRING)

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
        val text: CharSequence = weakResources.get()?.getString(R.string.my_list_empty_list_title).orEmpty()
        return UiEntityOfText(com.scotiabank.canvascore.R.style.canvascore_style_headline_small_black, Gravity.CENTER, text)
    }

    private fun createEntityOfDescriptionText(): UiEntityOfText {
        val text: CharSequence = createText()
        return UiEntityOfText(com.scotiabank.canvascore.R.style.canvascore_style_body2, Gravity.CENTER, text)
    }

    private fun createText(): SpannableStringBuilder {
        val boldText: String = weakResources.get()
            ?.getString(R.string.my_list_empty_list_description_bold)
            ?: return empty
        val fullText: String = weakResources.get()
            ?.getString(R.string.my_list_empty_list_description_full)
            ?: return empty
        return SpannableStringBuilder
            .valueOf(fullText)
            .setTypefaceSpan(appModel.boldTypeface, boldText)
    }
}