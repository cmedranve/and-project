package pe.com.scotiabank.blpm.android.client.base.calltoaction

import android.view.Gravity
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText

class CollectorOfOneColumnContent(
    private val factory: FactoryOfOneColumnTextEntity,
    private val paddingEntity: UiEntityOfPadding,
    private val title: String,
    private val description: CharSequence,
): CollectorOfOneColumnText {

    override fun collect(): List<UiEntityOfOneColumnText> {

        val titleUiEntity = factory.create(
            paddingEntity = paddingEntity,
            appearance = R.style.canvascore_style_headline_small_black,
            text = title,
            gravity = Gravity.CENTER,
        )

        val descriptionUiEntity = factory.create(
            paddingEntity = paddingEntity,
            appearance = R.style.canvascore_style_body2_monospace_numbers,
            text = description,
            gravity = Gravity.CENTER,
        )

        return listOf(titleUiEntity, descriptionUiEntity)
    }
}
