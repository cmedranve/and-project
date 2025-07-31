package pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfTwoColumnText {
    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver? = null,
    ): List<UiEntityOfTwoColumnText>
}
