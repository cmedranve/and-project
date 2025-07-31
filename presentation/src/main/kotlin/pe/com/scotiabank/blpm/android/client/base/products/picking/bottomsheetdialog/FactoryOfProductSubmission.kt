package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog

import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton.CollectorOfProductRadioButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobutton.AdapterFactoryOfRadioButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

class FactoryOfProductSubmission(
    private val collectorOfProductRadioButton: CollectorOfProductRadioButton
): FactoryOfTypeSubmission {

    override fun createTypes(
        products: List<ProductModel>,
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ): List<UiCompound<*>> {

        val dataOfProductSubmission = createDataOfProductSubmission(
            products,
            controllerOfRadioButton,
            controllerOfChip
        )

        return listOf(dataOfProductSubmission)
    }

    private fun createDataOfProductSubmission(
        products: List<ProductModel>,
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ): UiCompound<UiEntityOfCheckableButton<ProductModel>> {

        val entities: List<UiEntityOfCheckableButton<ProductModel>> = collectorOfProductRadioButton
            .collect(products, controllerOfRadioButton, controllerOfChip)

        val adapterFactory: AdapterFactoryOfRadioButton<ProductModel> = AdapterFactoryOfRadioButton()

        return UiCompound(entities, adapterFactory)
    }
}
