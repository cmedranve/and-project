package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub

import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText

class CollectorOfTitle(
    private val factory: FactoryOfOneColumnTextEntity,
    private val paddingEntity: UiEntityOfPadding,
    private val title: CharSequence,
) : CollectorOfOneColumnText {

    override fun collect(): List<UiEntityOfOneColumnText> {

        val titleEntity = factory.create(
            paddingEntity = paddingEntity,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_subtitle1,
            text = title,
        )

        return listOf(titleEntity)
    }
}
