package pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton

import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection

interface ProductGroupService {

    val controller: ControllerOfSingleSelection<ProductModel>

    fun add(productGroup: ProductGroup)
}
