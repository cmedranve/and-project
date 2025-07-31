package pe.com.scotiabank.blpm.android.client.base.onecolumn

import android.view.Gravity
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.CollectorOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText

class CollectOfOneColumnGeneric constructor(
    private val receiver: InstanceReceiver? = null,
    private val textBuilder: TextBuilder? = null,
    private val text: CharSequence = Constant.EMPTY_STRING,
    private val gravity: Int = Gravity.CENTER,
    private val appearance: Int = R.style.canvascore_style_headline_18,
    private val data: Any? = null,
    private val id: Long = randomLong(),
) : CollectorOfOneColumnText {

    val entity: UiEntityOfText by lazy {
        val textDefault: String = text.toString()

        UiEntityOfText(
            appearance = appearance,
            gravity = gravity,
            text = textBuilder?.build(textDefault) ?: textDefault,
            receiver = receiver,
            data = data,
            id = id,
        )
    }

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?,
    ): List<UiEntityOfOneColumnText> {

        val uiEntityOfOneColumnText = UiEntityOfOneColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn = entity,
        )

        return listOf(uiEntityOfOneColumnText)
    }

    fun editText(text: CharSequence) {
        entity.text = text
    }
}
