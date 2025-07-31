package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled

import android.content.res.Resources
import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class CollectorOfOneColumnText(
    private val weakResources: WeakReference<Resources?>,
    private val horizontalPaddingEntity: UiEntityOfPadding,
    private val factory: FactoryOfOneColumnTextEntity = FactoryOfOneColumnTextEntity(),
) {

    private val paddingEntityForText: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
        )
    }

    fun collect(): List<UiEntityOfOneColumnText> {

        val titleEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForText,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_headline_small_black,
            text = weakResources.get()?.getString(R.string.google_wallet_not_installed).orEmpty(),
            gravity = Gravity.CENTER,
        )

        val descriptionEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForText,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            text = weakResources.get()?.getString(R.string.google_wallet_not_installed_detail).orEmpty(),
            gravity = Gravity.CENTER,
        )

        return listOf(titleEntity, descriptionEntity)
    }
}
