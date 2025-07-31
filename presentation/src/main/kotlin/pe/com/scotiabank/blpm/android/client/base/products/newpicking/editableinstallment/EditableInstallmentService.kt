package pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment

import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText

interface EditableInstallmentService {

    fun clearEditableInstallments()

    fun addEditableInstallment(editableInstallment: EditableInstallment)

    fun findEditTextEntityBy(id: Long): UiEntityOfEditText<EditableInstallment>?
}
