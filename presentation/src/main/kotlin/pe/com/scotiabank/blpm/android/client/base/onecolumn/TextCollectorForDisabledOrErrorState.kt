package pe.com.scotiabank.blpm.android.client.base.onecolumn

import android.content.res.Resources
import android.view.Gravity
import androidx.annotation.StringRes
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import java.lang.ref.WeakReference

class TextCollectorForDisabledOrErrorState(
    paddingEntity: UiEntityOfPadding,
    private val weakResources: WeakReference<Resources?>,
    @StringRes private val titleRes: Int,
    @StringRes private val descriptionRes: Int,
): CollectorOfOneColumnText {

    private val paddingEntity = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = R.dimen.canvascore_margin_16,
        bottom = R.dimen.canvascore_margin_16,
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
        val text: CharSequence = weakResources.get()?.getString(titleRes).orEmpty()
        return UiEntityOfText(R.style.canvascore_style_headline_small_black, Gravity.CENTER, text)
    }

    private fun createEntityOfDescriptionText(): UiEntityOfText {
        val text: CharSequence = weakResources.get()?.getString(descriptionRes).orEmpty()
        return UiEntityOfText(R.style.canvascore_style_body2, Gravity.CENTER, text)
    }
}
