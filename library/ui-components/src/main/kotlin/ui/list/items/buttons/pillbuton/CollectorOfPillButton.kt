package pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfPillButton {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver? = null,
    ): List<UiEntityOfPillButton<Any>>
}
