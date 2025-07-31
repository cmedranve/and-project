package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfQuickActionCard<D: Any> {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver? = null
    ): List<UiEntityOfQuickActionCard<D>>
}
