package pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

object EmptyCollectorOfOneColumnText : CollectorOfOneColumnText {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?
    ): List<UiEntityOfOneColumnText> = emptyList()
}
