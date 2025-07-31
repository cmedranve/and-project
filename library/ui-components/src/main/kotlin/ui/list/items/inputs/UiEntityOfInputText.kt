package pe.com.scotiabank.blpm.android.ui.list.items.inputs

import android.text.InputFilter
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.UiEntityOfClearButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip

interface UiEntityOfInputText<D: Any> {

    val paddingEntity: UiEntityOfPadding
    var titleText: String
    var text: CharSequence
    var hintText: CharSequence
    var supplementaryText: CharSequence
    var errorText: CharSequence
    val receiver: InstanceReceiver?
    val inputType: Int
    val contentDescription: CharSequence
    val toolTipEntity: UiEntityOfToolTip?
    val data: D?
    var isEnabled: Boolean
    var filters: Array<InputFilter>
}
