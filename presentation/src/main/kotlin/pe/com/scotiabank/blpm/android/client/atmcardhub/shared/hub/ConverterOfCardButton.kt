package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub

import android.content.res.Resources
import android.view.Gravity
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import java.lang.ref.WeakReference

class ConverterOfCardButton(
    private val weakResources: WeakReference<Resources?>,
    private val receiver: InstanceReceiver,
) {

    private val paddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
        )
    }

    fun toUiEntity(
        atmCardAction: AtmCardAction
    ): UiEntityOfTextButton<Any> = UiEntityOfTextButton(
        paddingEntity = paddingEntity,
        isEnabled = true,
        text = weakResources.get()?.getString(atmCardAction.action.titleRes).orEmpty(),
        receiver = receiver,
        data = atmCardAction,
        appearanceForEnabledState = R.style.canvascore_style_text_button_black,
        gravity = Gravity.CENTER,
        drawableStartId = atmCardAction.action.iconRes,
        expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
    )
}
