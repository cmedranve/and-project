package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class ConverterOfLabel(
    private val weakResources: WeakReference<Resources?>,
    private val factory: FactoryOfOneColumnTextEntity,
    private val horizontalPaddingEntity: UiEntityOfPadding,
) {

    private val paddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
        )
    }

    fun toUiEntity(group: AtmCardGroup): UiEntityOfOneColumnText = factory.create(
        paddingEntity = paddingEntity,
        appearance = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        text = weakResources.get()?.getString(group.ownerType.labelRes).orEmpty(),
    )
}
