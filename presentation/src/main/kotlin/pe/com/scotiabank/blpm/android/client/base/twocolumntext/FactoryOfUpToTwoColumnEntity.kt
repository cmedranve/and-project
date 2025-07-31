package pe.com.scotiabank.blpm.android.client.base.twocolumntext

import android.text.TextUtils
import android.view.Gravity
import androidx.annotation.StyleRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText

class FactoryOfUpToTwoColumnEntity {

    fun create(
        paddingEntity: UiEntityOfPadding,
        maxLines1: Int = Int.MAX_VALUE,
        @StyleRes appearance1: Int,
        text1: CharSequence,
        maxLines2: Int = Int.MAX_VALUE,
        whereToEllipsize2: TextUtils.TruncateAt? = null,
        @StyleRes appearance2: Int,
        text2: CharSequence,
        placeHolderEntityForEmptyText: UiEntityOfText,
        guidelinePercent: Float,
        id: Long = randomLong(),
    ): UiEntityOfTwoColumnText {

        val entityOfColumn1 = if (text1.isBlank()) placeHolderEntityForEmptyText else UiEntityOfText(
            maxLines = maxLines1,
            appearance = appearance1,
            gravity = Gravity.START,
            text = text1,
            whereToEllipsize = TextUtils.TruncateAt.END,
        )
        val entityOfColumn2 = if (text2.isBlank()) placeHolderEntityForEmptyText else UiEntityOfText(
            maxLines = maxLines2,
            whereToEllipsize = whereToEllipsize2,
            appearance = appearance2,
            gravity = Gravity.END,
            text = text2,
        )
        return UiEntityOfTwoColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn1 = entityOfColumn1,
            entityOfColumn2 = entityOfColumn2,
            guidelinePercent = guidelinePercent,
            id = id,
        )
    }
}
