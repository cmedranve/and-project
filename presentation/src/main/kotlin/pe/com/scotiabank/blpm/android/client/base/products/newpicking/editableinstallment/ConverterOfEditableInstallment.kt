package pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment

import android.content.res.Resources
import android.text.InputFilter
import android.text.InputType
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference

class ConverterOfEditableInstallment(
    private val weakResources: WeakReference<Resources?>,
    private val horizontalPaddingEntity: UiEntityOfPadding,
    private val receiver: InstanceReceiver,
    private val installmentFieldId: Long,
) {

    private val paddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
        )
    }

    private val filters: Array<InputFilter> by lazy {
        val lengthFilter = InputFilter.LengthFilter(2)
        arrayOf(lengthFilter)
    }

    fun toUiEntity(
        editableInstallment: EditableInstallment,
    ): UiEntityOfEditText<EditableInstallment> {

        val entity: UiEntityOfEditText<EditableInstallment> = UiEntityOfEditText(
            paddingEntity = paddingEntity,
            titleText = weakResources.get()?.getString(R.string.installments).orEmpty(),
            hintText = weakResources.get()?.getString(R.string.installments_hint).orEmpty(),
            receiver = receiver,
            filters = filters,
            inputType = InputType.TYPE_CLASS_NUMBER,
            data = editableInstallment,
            id = installmentFieldId,
        )
        entity.supplementaryText = editableInstallment.rangeText
        return entity
    }
}
