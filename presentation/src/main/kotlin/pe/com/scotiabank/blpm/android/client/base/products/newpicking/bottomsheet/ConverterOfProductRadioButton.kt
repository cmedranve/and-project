package pe.com.scotiabank.blpm.android.client.base.products.newpicking.bottomsheet

import android.text.TextUtils
import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.InstallmentCollector
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.util.Constant
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
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText

class ConverterOfProductRadioButton(
    private val formatterOfProductName: FormatterOfProductName,
    private val installmentCollector: InstallmentCollector,
    private val factory: FactoryOfOneColumnTextEntity,
) {

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    private val paddingOfRadioButtonIcon: UiEntityOfPadding by lazy {
        UiEntityOfPadding(top = R.dimen.canvascore_margin_14)
    }

    private val paddingOfIncludedSide: UiEntityOfPadding by lazy {
        UiEntityOfPadding(top = R.dimen.canvascore_margin_12)
    }

    private val paddingOfIncludedItem: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_4,
            bottom = R.dimen.canvascore_margin_4,
        )
    }

    fun toUiEntities(
        productGroup: ProductGroup,
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ): List<UiEntityOfCheckableButton<ProductModel>> {

        val installmentEntities: List<UiEntityOfDynamicChipsComponent<Int>> = installmentCollector.collect(
            filteredProducts = productGroup.selectableProducts,
            paddingOfIncludedItem = paddingOfIncludedItem,
            controllerOfChip = controllerOfChip,
        )

        val entities: List<UiEntityOfCheckableButton<ProductModel>> = productGroup
            .selectableProducts
            .map { product -> toUiEntity(product, installmentEntities, controllerOfRadioButton) }

        entities
            .firstOrNull { entity -> isMatching(entity.data, productGroup.selectedProduct) }
            ?.let(controllerOfRadioButton::setDefaultItem)

        return entities
    }

    private fun toUiEntity(
        product: ProductModel,
        installmentEntities: List<UiEntityOfDynamicChipsComponent<Int>>,
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>
    ): UiEntityOfCheckableButton<ProductModel> {

        val includedSideEntity: UiEntityOfRecycler = createIncludeSideEntity(product)

        val bottomRecyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingOfIncludedSide,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )

        val entity: UiEntityOfCheckableButton<ProductModel> = UiEntityOfCheckableButton(
            paddingEntity = emptyPaddingEntity,
            paddingEntityOfCheckableIcon = paddingOfRadioButtonIcon,
            sideRecyclerEntity = includedSideEntity,
            bottomRecyclerEntity = bottomRecyclerEntity,
            controller = controllerOfRadioButton,
            data = product,
        )
        fillBottomRecyclerEntity(installmentEntities, product, entity)
        return entity
    }

    private fun createIncludeSideEntity(product: ProductModel): UiEntityOfRecycler {

        val text: CharSequence = formatterOfProductName.format(product)

        val oneColumnOfName: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingOfIncludedItem,
            appearance = R.style.canvascore_style_body2,
            text = text,
            maxLines = MAX_LINE_OF_NAME,
            whereToEllipsize = TextUtils.TruncateAt.MIDDLE,
        )

        val oneColumnTextCompound = UiCompound(
            uiEntities = listOf(oneColumnOfName),
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
        entity: UiEntityOfCheckableButton<ProductModel>
    ) {

        val installmentsOrEmpty: List<UiEntityOfDynamicChipsComponent<Int>> = getInstallmentEntitiesIfCreditCard(
            entities = installmentEntities,
            product = product,
        )

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
        product: ProductModel,
    ): List<UiEntityOfDynamicChipsComponent<Int>> {
        val isCreditCard: Boolean = isCreditCard(product)
        return if (isCreditCard) entities else emptyList()
    }

    private fun isCreditCard(
        product: ProductModel,
    ): Boolean = Constant.TC.equals(product.productType, ignoreCase = true)

    private fun isMatching(
        underEvaluation: ProductModel?,
        target: ProductModel,
    ): Boolean = target.id == underEvaluation?.id

    companion object {

        private val MAX_LINE_OF_NAME
            get() = 1
    }
}
