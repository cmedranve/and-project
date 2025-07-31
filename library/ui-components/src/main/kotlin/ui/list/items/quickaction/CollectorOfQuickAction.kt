package pe.com.scotiabank.blpm.android.ui.list.items.quickaction

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfQuickAction {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver? = null,
    ): List<UiEntityOfQuickAction<Any>>
}
