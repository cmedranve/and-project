package pe.com.scotiabank.blpm.android.ui.list.items.avatar

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfAvatar {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver? = null,
    ): List<UiEntityOfAvatar>
}
