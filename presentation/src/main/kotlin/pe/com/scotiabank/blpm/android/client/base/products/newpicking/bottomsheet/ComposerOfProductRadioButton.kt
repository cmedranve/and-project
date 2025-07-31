package pe.com.scotiabank.blpm.android.client.base.products.newpicking.bottomsheet

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobutton.AdapterFactoryOfRadioButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton

class ComposerOfProductRadioButton(
    private val converter: ConverterOfProductRadioButton,
    receiver: InstanceReceiver,
): ProductGroupService {

    override val controllerOfRadioButton: ControllerOfSingleSelection<ProductModel> = ControllerOfSingleSelection(
        instanceReceiver = receiver,
    )

    override val controllerOfChip: SelectionControllerOfChipsComponent<Int> = SelectionControllerOfChipsComponent(
        instanceReceiver = receiver,
    )

    private val entities: MutableList<UiEntityOfCheckableButton<ProductModel>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfCheckableButton<ProductModel>> {

        val adapterFactory: AdapterFactoryOfRadioButton<ProductModel> = AdapterFactoryOfRadioButton()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun add(productGroup: ProductGroup) {
        val newEntities: List<UiEntityOfCheckableButton<ProductModel>> = converter.toUiEntities(
            productGroup = productGroup,
            controllerOfRadioButton = controllerOfRadioButton,
            controllerOfChip = controllerOfChip,
        )
        entities.addAll(newEntities)
    }
}
