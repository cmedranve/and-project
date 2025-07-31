package pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.AdapterFactoryOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText

class ComposerOfEditableInstallment(
    private val converter: ConverterOfEditableInstallment,
) : EditableInstallmentService {

    private val entities: MutableList<UiEntityOfEditText<EditableInstallment>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfEditText<EditableInstallment>> {

        val adapterFactory: AdapterFactoryOfEditText<EditableInstallment> = AdapterFactoryOfEditText()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun clearEditableInstallments() {
        entities.clear()
    }

    override fun addEditableInstallment(editableInstallment: EditableInstallment) {
        val newEntity: UiEntityOfEditText<EditableInstallment> = converter.toUiEntity(
            editableInstallment = editableInstallment,
        )
        entities.add(newEntity)
    }

    override fun findEditTextEntityBy(id: Long): UiEntityOfEditText<EditableInstallment>? {
        return entities.firstOrNull { entity -> entity.id == id }
    }
}
