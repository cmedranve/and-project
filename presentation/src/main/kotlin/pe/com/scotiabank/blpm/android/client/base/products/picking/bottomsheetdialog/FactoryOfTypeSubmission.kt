package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog

import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

interface FactoryOfTypeSubmission {

    fun createTypes(
        products: List<ProductModel>,
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ): List<UiCompound<*>>
}
