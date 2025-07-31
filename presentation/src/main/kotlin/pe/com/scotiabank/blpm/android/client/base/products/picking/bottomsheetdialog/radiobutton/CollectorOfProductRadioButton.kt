package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton

import android.text.TextUtils
import android.view.Gravity
import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.InstallmentCollector
import pe.com.scotiabank.blpm.android.client.util.*
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.AdapterFactoryOfDynamicChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfDynamicChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText

class CollectorOfProductRadioButton(
    private val formatterOfProductName: FormatterOfProductName,
    private val installmentCollector: InstallmentCollector,
) {

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    private val paddingOfRadioButtonIcon: UiEntityOfPadding by lazy {
        UiEntityOfPadding(R.dimen.canvascore_margin_14)
    }
    private val paddingOfIncludedSide: UiEntityOfPadding by lazy {
        UiEntityOfPadding(R.dimen.canvascore_margin_12)
    }
    private val paddingOfIncludedBottom: UiEntityOfPadding by lazy {
        UiEntityOfPadding(R.dimen.canvascore_margin_0, R.dimen.canvascore_margin_6)
    }
    private val paddingOfIncludedItem: UiEntityOfPadding by lazy {
        UiEntityOfPadding(R.dimen.canvascore_margin_4, R.dimen.canvascore_margin_4)
    }

    fun collect(
        products: List<ProductModel>,
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ): List<UiEntityOfCheckableButton<ProductModel>> {

        val filteredProducts: List<ProductModel> = products
            .filter(::filterInProduct)
        if (filteredProducts.isEmpty()) return emptyList()

        val installmentEntities = installmentCollector.collect(
            filteredProducts,
            paddingOfIncludedItem,
            controllerOfChip
        )
        val items: List<UiEntityOfCheckableButton<ProductModel>> = filteredProducts
            .map { product ->
                toUiEntityOfRadioButton(
                    installmentEntities,
                    product,
                    controllerOfRadioButton
                )
            }

        controllerOfRadioButton.setDefaultItem(items.first())

        return items
    }

    private fun filterInProduct(product: ProductModel): Boolean {
        return product.symbolCurrency != null && product.symbolCurrency.isNotBlank()
    }

    private fun toUiEntityOfRadioButton(
        installmentEntities: List<UiEntityOfDynamicChipsComponent<Int>>,
        product: ProductModel,
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>
    ): UiEntityOfCheckableButton<ProductModel> {

        val sideRecyclerEntity = createSideRecyclerEntity(product)
        val bottomRecyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingOfIncludedBottom,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )

        val entity = UiEntityOfCheckableButton(
            paddingEntity = emptyPaddingEntity,
            paddingEntityOfCheckableIcon = paddingOfRadioButtonIcon,
            sideRecyclerEntity = sideRecyclerEntity,
            bottomRecyclerEntity = bottomRecyclerEntity,
            controller = controllerOfRadioButton,
            data = product,
        )
        fillBottomRecyclerEntity(installmentEntities, product, entity)
        return entity
    }

    private fun createSideRecyclerEntity(product: ProductModel): UiEntityOfRecycler {
        val text: CharSequence = formatterOfProductName.format(product)
        val textEntityOfName = UiEntityOfText(
            appearance = R.style.canvascore_style_body2,
            gravity = Gravity.START,
            text = text,
            maxLines = MAX_LINE_OF_NAME,
            whereToEllipsize = TextUtils.TruncateAt.MIDDLE,
        )
        val nameEntity = UiEntityOfOneColumnText(paddingOfIncludedItem, textEntityOfName)
        val oneColumnTextCompound = UiCompound(
            uiEntities = listOf(nameEntity),
            factoryOfPortableAdapter = AdapterFactoryOfOneColumnText(),
        )

        val compounds: List<UiCompound<*>> = listOf(oneColumnTextCompound)

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingOfIncludedSide,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    private fun fillBottomRecyclerEntity(
        installmentEntities: List<UiEntityOfDynamicChipsComponent<Int>>,
        product: ProductModel,
        entity: UiEntityOfCheckableButton<ProductModel>,
    ) {
        val installmentsOrEmpty = getInstallmentEntitiesIfCreditCard(installmentEntities, product)
        val dynamicChipsComponentCompound = UiCompound(
            uiEntities = installmentsOrEmpty,
            factoryOfPortableAdapter = AdapterFactoryOfDynamicChipsComponent(),
            visibilitySupplier = Supplier(entity::isChecked),
        )

        val compounds: List<UiCompound<*>> = listOf(dynamicChipsComponentCompound)
        compounds.associateByTo(destination = entity.bottomRecyclerEntity.compoundsById, keySelector = ::byId)
    }

    private fun getInstallmentEntitiesIfCreditCard(
        entities: List<UiEntityOfDynamicChipsComponent<Int>>,
        product: ProductModel
    ): List<UiEntityOfDynamicChipsComponent<Int>> {
        val isCreditCard: Boolean = isCreditCard(product)
        return if (isCreditCard) entities else emptyList()
    }

    private fun isCreditCard(product: ProductModel): Boolean {
        return Constant.TC.equals(product.productType, ignoreCase = true)
    }

    companion object {

        private val MAX_LINE_OF_NAME
            get() = 1
    }
}
