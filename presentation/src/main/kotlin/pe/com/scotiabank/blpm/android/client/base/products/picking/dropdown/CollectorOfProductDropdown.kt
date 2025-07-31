package pe.com.scotiabank.blpm.android.client.base.products.picking.dropdown

import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown.SelectionControllerOfDropdownSelector
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown.UiEntityOfDropdownSelector
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown.UiEntityOfDropdownSelectorItem

class CollectorOfProductDropdown(private val formatterOfProductName: FormatterOfProductName) {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        controllerOfDropdown: SelectionControllerOfDropdownSelector<ProductModel>,
        products: List<ProductModel>,
        titleText: CharSequence,
    ): List<UiEntityOfDropdownSelector<ProductModel>> {

        val filteredProducts: List<ProductModel> = products
            .filter(::filterInProduct)
        if (filteredProducts.isEmpty()) return emptyList()

        val itemEntities: MutableList<UiEntityOfDropdownSelectorItem<ProductModel>> = mutableListOf()
        filteredProducts.mapTo(itemEntities, ::toUiEntityOfDropdownSelectorItem)

        val selectorEntity: UiEntityOfDropdownSelector<ProductModel> = UiEntityOfDropdownSelector(
            paddingEntity = paddingEntity,
            controller = controllerOfDropdown,
            itemEntities = itemEntities,
            isFirstItemHint = false,
            titleText = titleText,
        )
        controllerOfDropdown.setSelectorEntity(selectorEntity)
        itemEntities.firstOrNull()?.let(controllerOfDropdown::setDefaultItem)

        return listOf(selectorEntity)
    }

    private fun filterInProduct(product: ProductModel): Boolean {
        return product.symbolCurrency != null && product.symbolCurrency.isNotBlank()
    }

    private fun toUiEntityOfDropdownSelectorItem(
        product: ProductModel,
    ): UiEntityOfDropdownSelectorItem<ProductModel> {

        val text: CharSequence = formatterOfProductName.format(product)

        return  UiEntityOfDropdownSelectorItem(
            text = text.toString(),
            data = product,
        )
    }
}
