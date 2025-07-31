package pe.com.scotiabank.blpm.android.client.base.twocolumntext

import android.text.TextUtils
import android.view.Gravity
import androidx.annotation.StyleRes
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText

class FactoryOfTwoColumnEntity {

    fun create(
        paddingEntity: UiEntityOfPadding,
        @StyleRes appearance1: Int,
        text1: CharSequence,
        gravity1: Int = Gravity.START,
        @StyleRes appearance2: Int,
        text2: CharSequence,
        gravity2: Int = Gravity.END,
        guidelinePercent: Float,
    ) : UiEntityOfTwoColumnText {

        val entityOfColumn1 = UiEntityOfText(
            appearance = appearance1,
            gravity = gravity1,
            text = text1,
            whereToEllipsize = TextUtils.TruncateAt.END,
        )
        val entityOfColumn2 = UiEntityOfText(
            appearance = appearance2,
            gravity = gravity2,
            text = text2,
        )
        return UiEntityOfTwoColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn1 = entityOfColumn1,
            entityOfColumn2 = entityOfColumn2,
            guidelinePercent = guidelinePercent,
        )
    }
}
