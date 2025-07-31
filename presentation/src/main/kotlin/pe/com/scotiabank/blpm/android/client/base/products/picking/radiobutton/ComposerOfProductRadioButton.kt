package pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobuttoncard.AdapterFactoryOfRadioButtonCard

class ComposerOfProductRadioButton(
    private val converter: ConverterOfProductRadioButton,
    receiver: InstanceReceiver,
) : ProductGroupService {

    override val controller: ControllerOfSingleSelection<ProductModel> = ControllerOfSingleSelection(
        instanceReceiver = receiver,
    )

    private val entities: MutableList<UiEntityOfCheckableButton<ProductModel>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfCheckableButton<ProductModel>> {

        val adapterFactory: AdapterFactoryOfRadioButtonCard<ProductModel> = AdapterFactoryOfRadioButtonCard()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun add(productGroup: ProductGroup) {
        val newEntities: List<UiEntityOfCheckableButton<ProductModel>> = converter.toUiEntities(
            productGroup = productGroup,
            controller = controller
        )
        entities.addAll(newEntities)
    }
}
