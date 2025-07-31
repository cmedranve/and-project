package pe.com.scotiabank.blpm.android.client.base.products.newpicking.bottomsheet

import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection

interface ProductGroupService {

    val controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>

    val controllerOfChip: SelectionControllerOfChipsComponent<Int>

    fun add(productGroup: ProductGroup)
}
