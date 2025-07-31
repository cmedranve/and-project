package pe.com.scotiabank.blpm.android.client.base.onecolumn

import android.text.TextUtils
import android.text.method.MovementMethod
import android.view.Gravity
import androidx.annotation.StyleRes
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

class FactoryOfOneColumnTextEntity {

    fun create(
        paddingEntity: UiEntityOfPadding,
        @StyleRes appearance: Int,
        text: CharSequence,
        gravity: Int = Gravity.START,
        whereToEllipsize: TextUtils.TruncateAt? = null,
        movementMethod: MovementMethod? = null,
        receiver: InstanceReceiver? = null,
        data: Any? = null,
        maxLines: Int = Int.MAX_VALUE,
        expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
        id: Long = randomLong(),
    ): UiEntityOfOneColumnText {

        val textEntity = UiEntityOfText(
            appearance = appearance,
            gravity = gravity,
            text = text,
            whereToEllipsize = whereToEllipsize,
            movementMethod = movementMethod,
            receiver = receiver,
            data = data,
            maxLines = maxLines,
        )

        return UiEntityOfOneColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn = textEntity,
            expectedFlexGrow = expectedFlexGrow,
            id = id,
        )
    }
}
