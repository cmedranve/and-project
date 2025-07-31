package pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton

import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.emptyUiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.util.BIAS_AT_MIDDLE

class ConverterOfProductRadioButton(
    private val horizontalPaddingEntity: UiEntityOfPadding,
    private val currencyFormatter: CurrencyFormatter,
    private val formatterOfProductName: FormatterOfProductName,
    private val factory: FactoryOfOneColumnTextEntity,
) {

    private val paddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_8,
            bottom = R.dimen.canvascore_margin_8,
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
        )
    }

    private val paddingEntityForCheckableIcon: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_16,
            bottom = R.dimen.canvascore_margin_16,
            left = R.dimen.canvascore_margin_16,
        )
    }

    private val paddingEntityForRecycler: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_14,
            bottom = R.dimen.canvascore_margin_14,
            right = R.dimen.canvascore_margin_16,
        )
    }

    private val paddingEntityForText: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_2,
            bottom = R.dimen.canvascore_margin_2,
        )
    }

    fun toUiEntities(
        productGroup: ProductGroup,
        controller: ControllerOfSingleSelection<ProductModel>,
    ): List<UiEntityOfCheckableButton<ProductModel>> {

        val entities: List<UiEntityOfCheckableButton<ProductModel>> = productGroup
            .selectableProducts
            .map { product -> toUiEntity(product, controller) }

        entities
            .firstOrNull { entity -> isMatching(entity.data, productGroup.selectedProduct) }
            ?.let(controller::setDefaultItem)

        return entities
    }

    private fun toUiEntity(
        product: ProductModel,
        controller: ControllerOfSingleSelection<ProductModel>,
    ): UiEntityOfCheckableButton<ProductModel> {

        val sideRecyclerEntity = createHorizontalPaymentEntity(product)

        return UiEntityOfCheckableButton(
            paddingEntity = paddingEntity,
            verticalBiasOfCheckableIcon = BIAS_AT_MIDDLE,
            paddingEntityOfCheckableIcon = paddingEntityForCheckableIcon,
            sideRecyclerEntity = sideRecyclerEntity,
            bottomRecyclerEntity = emptyUiEntityOfRecycler,
            controller = controller,
            data = product,
        )
    }

    private fun createHorizontalPaymentEntity(product: ProductModel): UiEntityOfRecycler {

        val nameEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForText,
            appearance = R.style.canvascore_style_caption_alternate,
            text = formatterOfProductName.findName(product).orEmpty(),
        )

        val amountEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForText,
            appearance = R.style.canvascore_style_body2,
            text = currencyFormatter.format(product.currencyId, product.availableAmount),
        )
        val oneColumnTextCompound = UiCompound(
            uiEntities = listOf(nameEntity, amountEntity),
            factoryOfPortableAdapter = AdapterFactoryOfOneColumnText(),
        )

        val compounds: List<UiCompound<*>> = listOf(oneColumnTextCompound)

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingEntityForRecycler,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    private fun isMatching(
        underEvaluation: ProductModel?,
        target: ProductModel,
    ): Boolean = target.id == underEvaluation?.id

}
