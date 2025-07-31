package pe.com.scotiabank.blpm.android.client.base.products.picking.dropdown

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown.AdapterFactoryOfDropdownSelector
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown.SelectionControllerOfDropdownSelector
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown.UiEntityOfDropdownSelector
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

class ComposerOfProductDropdown(
    private val collector: CollectorOfProductDropdown,
    receiver: InstanceReceiver,
) {

    val controller: SelectionControllerOfDropdownSelector<ProductModel> = SelectionControllerOfDropdownSelector(
        instanceReceiver = receiver,
    )
    val selectedProduct: ProductModel?
        get() = controller.selectedItem?.data

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        products: List<ProductModel>,
        titleText: CharSequence,
    ): UiCompound<UiEntityOfDropdownSelector<ProductModel>> {

        val entities: List<UiEntityOfDropdownSelector<ProductModel>> = collector.collect(
            paddingEntity = paddingEntity,
            controllerOfDropdown = controller,
            products = products,
            titleText = titleText,
        )
        val adapterFactory: AdapterFactoryOfDropdownSelector<ProductModel> = AdapterFactoryOfDropdownSelector()

        return UiCompound(entities, adapterFactory)
    }
}
