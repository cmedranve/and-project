package pe.com.scotiabank.blpm.android.client.base.labelbutton

import android.text.TextUtils
import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.labelbuttonpair.UiEntityOfLabelButtonPair
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText

class FactoryOfLabelButtonEntity(private val receiver: InstanceReceiver) {

    private val emptyPadding: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    fun <D: Any> create(
        paddingEntity: UiEntityOfPadding,
        @StyleRes labelAppearance: Int,
        labelGravity: Int = Gravity.START,
        labelText: CharSequence = Constant.EMPTY_STRING,
        whereToEllipsize: TextUtils.TruncateAt? = null,
        isEnabled: Boolean = true,
        buttonText: CharSequence = Constant.EMPTY_STRING,
        data: D? = null,
        @DrawableRes drawableStartId: Int = ResourcesCompat.ID_NULL,
        @DrawableRes drawableEndId: Int = ResourcesCompat.ID_NULL,
    ): UiEntityOfLabelButtonPair<D> {

        val labelEntity = UiEntityOfText(
            appearance = labelAppearance,
            gravity = labelGravity,
            text = labelText,
            whereToEllipsize = whereToEllipsize,
        )

        val buttonEntity = UiEntityOfTextButton(
            paddingEntity = emptyPadding,
            isEnabled = isEnabled,
            text = buttonText,
            receiver = receiver,
            data = data,
            drawableStartId = drawableStartId,
            drawableEndId = drawableEndId,
        )

        return UiEntityOfLabelButtonPair(
            paddingEntity = paddingEntity,
            labelEntity = labelEntity,
            buttonEntity = buttonEntity,
        )
    }
}
