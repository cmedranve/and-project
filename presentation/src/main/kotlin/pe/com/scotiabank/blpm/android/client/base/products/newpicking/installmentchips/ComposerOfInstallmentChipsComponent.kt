package pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.AdapterFactoryOfStaticChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.HolderOfChipController
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfStaticChipsComponent

class ComposerOfInstallmentChipsComponent(
    private val converter: CollectorOfInstallmentChipsComponent,
    receiver: InstanceReceiver,
) : HolderOfChipController<InstallmentOption> {

    override val chipController: SelectionControllerOfChipsComponent<InstallmentOption> = SelectionControllerOfChipsComponent(
        instanceReceiver = receiver,
    )

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ) : UiCompound<UiEntityOfStaticChipsComponent<InstallmentOption>> {

        val entities: List<UiEntityOfStaticChipsComponent<InstallmentOption>> = converter.collect(
            controller = chipController,
        )
        val adapterFactory: AdapterFactoryOfStaticChipsComponent<InstallmentOption> = AdapterFactoryOfStaticChipsComponent()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
