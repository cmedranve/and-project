package pe.com.scotiabank.blpm.android.client.newdashboard.edit

import android.text.InputType
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.operation.IdentifiableEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.CollectorOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip

class CollectorOfEditableName(
    private val helper: HelperForOperation,
): CollectorOfEditText<IdentifiableEditText> {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver,
        toolTipEntity: UiEntityOfToolTip?,
    ): List<UiEntityOfEditText<IdentifiableEditText>> {

        val editEntity: UiEntityOfEditText<IdentifiableEditText> = UiEntityOfEditText(
            paddingEntity = paddingEntity,
            titleText = helper.titleText,
            hintText = helper.hintText,
            receiver = receiver,
            filters = helper.filters,
            inputType = InputType.TYPE_CLASS_TEXT,
            toolTipEntity = toolTipEntity,
            data = helper.data,
        )

        return listOf(editEntity)
    }
}