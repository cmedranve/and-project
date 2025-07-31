package pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip

interface CollectorOfEditText<D: Any> {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver,
        toolTipEntity: UiEntityOfToolTip? = null,
    ): List<UiEntityOfEditText<D>>
}
