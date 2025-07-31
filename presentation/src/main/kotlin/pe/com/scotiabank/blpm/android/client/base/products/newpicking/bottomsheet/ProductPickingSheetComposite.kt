package pe.com.scotiabank.blpm.android.client.base.products.newpicking.bottomsheet

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.InstallmentCollector
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.util.concurrent.ConcurrentHashMap

class ProductPickingSheetComposite(
    dispatcherProvider: DispatcherProvider,
    private val composerOfProductRadioButton: ComposerOfProductRadioButton,
    uiStateHolder: UiStateHolder,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    UiStateHolder by uiStateHolder,
    ProductGroupService by composerOfProductRadioButton
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val radioButtonCompound = composerOfProductRadioButton.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )

        return listOf(radioButtonCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val formatterOfProductName: FormatterOfProductName,
        private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity,
        private val installmentCollector: InstallmentCollector,
        private val uiStateHolder: UiStateHolder,
    ) {

        fun create(
            receiver: InstanceReceiver,
        ): ProductPickingSheetComposite = ProductPickingSheetComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfProductRadioButton = createComposerOfProductRadioButton(receiver),
            uiStateHolder = uiStateHolder,
        )

        private fun createComposerOfProductRadioButton(
            receiver: InstanceReceiver,
        ): ComposerOfProductRadioButton {

            val converter = ConverterOfProductRadioButton(
                formatterOfProductName = formatterOfProductName,
                factory = factoryOfOneColumnTextEntity,
                installmentCollector = installmentCollector,
            )
            return ComposerOfProductRadioButton(converter, receiver)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
