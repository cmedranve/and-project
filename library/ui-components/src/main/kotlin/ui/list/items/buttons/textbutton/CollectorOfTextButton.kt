package pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfTextButton {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver? = null,
    ): List<UiEntityOfTextButton<Any>>
}
