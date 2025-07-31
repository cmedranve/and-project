package pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips

import pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment.EditableInstallment

enum class InstallmentOption(
    val label: String,
    val editableInstallment: EditableInstallment?,
) {

    IN_FULL(
        label = "Sin cuotas",
        editableInstallment = null,
    ),

    IN_INSTALLMENTS(
        label = "En cuotas",
        editableInstallment = EditableInstallment(),
    );
}
