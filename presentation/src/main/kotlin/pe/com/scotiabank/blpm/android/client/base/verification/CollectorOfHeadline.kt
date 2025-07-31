package pe.com.scotiabank.blpm.android.client.base.verification

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class CollectorOfHeadline(
    private val weakResources: WeakReference<Resources?>,
    private val paddingEntity: UiEntityOfPadding,
    private val factory: FactoryOfOneColumnTextEntity,
) {

    fun collect(): List<UiEntityOfOneColumnText> {
        val entity = factory.create(
            paddingEntity = paddingEntity,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_headline_small_black,
            text = weakResources.get()?.getString(R.string.digital_key_verification_message).orEmpty()
        )
        return listOf(entity)
    }
}
