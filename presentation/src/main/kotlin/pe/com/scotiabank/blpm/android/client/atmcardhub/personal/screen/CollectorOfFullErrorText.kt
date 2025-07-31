package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import java.lang.ref.WeakReference

class CollectorOfFullErrorText(
    private val weakResources: WeakReference<Resources?>,
    private val factory: FactoryOfOneColumnTextEntity,
) : CollectorOfOneColumnText {

    private val paddingEntityOfTitle: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_48,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_18
        )
    }

    private val paddingEntityOfDescription: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    override fun collect(): List<UiEntityOfOneColumnText> {

        val titleEntity = factory.create(
            paddingEntity = paddingEntityOfTitle,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_headline_small_black,
            text = weakResources.get()?.getString(R.string.error_hub_title).orEmpty(),
            gravity = Gravity.CENTER,
        )

        val descriptionEntity = factory.create(
            paddingEntity = paddingEntityOfDescription,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            text = weakResources.get()?.getString(R.string.error_hub_info).orEmpty(),
            gravity = Gravity.CENTER,
        )

        return listOf(titleEntity, descriptionEntity)
    }
}
