package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog

import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfDynamicChipsComponent

fun interface InstallmentCollector {

    fun collect(
        filteredProducts: List<ProductModel>,
        paddingOfIncludedItem: UiEntityOfPadding,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>,
    ): List<UiEntityOfDynamicChipsComponent<Int>>
}
