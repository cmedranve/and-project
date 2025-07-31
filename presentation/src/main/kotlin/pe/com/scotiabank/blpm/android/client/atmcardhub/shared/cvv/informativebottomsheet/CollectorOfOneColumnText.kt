package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.informativebottomsheet

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import java.lang.ref.WeakReference

class CollectorOfOneColumnText(
    private val weakResources: WeakReference<Resources?>,
    private val factory: FactoryOfOneColumnTextEntity,
    private val paddingEntity: UiEntityOfPadding,
) : CollectorOfOneColumnText {

    private val paddingEntityOfSubtitle: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = paddingEntity.left,
            right = paddingEntity.right,
        )
    }

    override fun collect(): List<UiEntityOfOneColumnText> {

        val descriptionEntity = factory.create(
            paddingEntity = paddingEntity,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_caption,
            text = weakResources.get()?.getString(R.string.dynamic_cvv_description).orEmpty(),
        )

        val subtitleEntity = factory.create(
            paddingEntity = paddingEntityOfSubtitle,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_subtitle3,
            text = weakResources.get()?.getString(R.string.how_does_it_work).orEmpty(),
        )

        return listOf(descriptionEntity, subtitleEntity)
    }
}
