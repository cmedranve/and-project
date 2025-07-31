package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import android.content.res.Resources
import android.view.Gravity
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

    override fun collect(): List<UiEntityOfOneColumnText> {

        val titleEntity = factory.create(
            paddingEntity = paddingEntity,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_headline_small_black,
            text = weakResources.get()?.getString(R.string.cvv_onboarding_title).orEmpty(),
            gravity = Gravity.CENTER,
        )

        val descriptionEntity = factory.create(
            paddingEntity = paddingEntity,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            text = weakResources.get()?.getString(R.string.cvv_onboarding_description).orEmpty(),
            gravity = Gravity.CENTER,
        )

        return listOf(titleEntity, descriptionEntity)
    }
}
