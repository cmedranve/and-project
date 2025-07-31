package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class EmptyCollectorOfQuickActionCard : CollectorOfQuickActionCard<Any> {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?,
    ): List<UiEntityOfQuickActionCard<Any>> = emptyList()
}
