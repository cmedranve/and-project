package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment.ComposerOfEditableInstallment
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment.ConverterOfEditableInstallment
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment.EditableInstallmentService
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.exchangerate.ComposerOfExchangeRate
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.exchangerate.ConverterOfExchangeRate
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.exchangerate.ExchangeRateService
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips.CollectorOfInstallmentChipsComponent
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips.ComposerOfInstallmentChipsComponent
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips.InstallmentOption
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ComposerOfProductRadioButton
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ConverterOfProductRadioButton
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroupService
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.HolderOfChipController
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val composerOfSkeleton: ComposerOfSkeleton,
    private val composerOfHeader: ComposerOfOneColumnText,
    private val composerOfProductRadioButton: ComposerOfProductRadioButton,
    private val composerOfExchangeRate: ComposerOfExchangeRate,
    private val composerOfInstallmentChipsComponent: ComposerOfInstallmentChipsComponent,
    private val composerOfEditableInstallment: ComposerOfEditableInstallment,
) : DispatcherProvider by dispatcherProvider,
    UiStateHolder by uiStateHolder,
    ProductGroupService by composerOfProductRadioButton,
    ExchangeRateService by composerOfExchangeRate,
    HolderOfChipController<InstallmentOption> by composerOfInstallmentChipsComponent,
    EditableInstallmentService by composerOfEditableInstallment,
    CompositeForProductPicking
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    private val isCreditCardChecked: Boolean
        get() = Constant.TC.contentEquals(
            charSequence = controller.selectedItem?.data?.productType?.uppercase().orEmpty(),
        )

    private val isNonCreditCardChecked: Boolean
        get() = isCreditCardChecked.not()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {
        val mutableCompounds: MutableList<UiCompound<*>> = mutableListOf()

        val skeletonCompound = composerOfSkeleton.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible)
        )
        mutableCompounds.add(skeletonCompound)

        val headerCompound = composerOfHeader.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        mutableCompounds.add(headerCompound)

        val productRadioButtonCompound = composerOfProductRadioButton.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )
        mutableCompounds.add(productRadioButtonCompound)

        val exchangeRateCompound = composerOfExchangeRate.composeUiData(
            visibilitySupplier = Supplier(::isNonCreditCardChecked),
        )
        mutableCompounds.add(exchangeRateCompound)

        val installmentChipsComponentCompound = composerOfInstallmentChipsComponent.composeUiData(
            visibilitySupplier = Supplier(::isCreditCardChecked)
        )
        mutableCompounds.add(installmentChipsComponentCompound)

        val editableInstallmentCompound = composerOfEditableInstallment.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )
        mutableCompounds.add(editableInstallmentCompound)

        return mutableCompounds
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val uiStateHolder: UiStateHolder,
        private val idRegistry: IdRegistry,
        private val appModel: AppModel,
        private val weakResources: WeakReference<Resources?>,
        private val amountFormatter: CurrencyFormatter,
        private val exchangeRateFormatter: CurrencyFormatter,
        private val formatterOfProductName: FormatterOfProductName,
        private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity,
        private val carrier: CarrierFromPickingConsumer,
    ): CompositeForProductPicking.Factory {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = R.dimen.canvascore_margin_16,
                right = R.dimen.canvascore_margin_16,
            )
        }

        override fun create(receiver: InstanceReceiver): MainTopComposite = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            composerOfSkeleton = createComposerOfSkeleton(),
            composerOfHeader = createComposerOfHeader(),
            composerOfProductRadioButton = createComposerOfProductRadioButton(receiver),
            composerOfExchangeRate = createComposerOfExchangeRate(),
            composerOfInstallmentChipsComponent = createComposerOfInstallmentChipsComponent(receiver),
            composerOfEditableInstallment = createComposerOfEditableInstallment(receiver),
        )

        private fun createComposerOfSkeleton(): ComposerOfSkeleton {
            val collector = SkeletonCollectorForLoading(horizontalPaddingEntity)
            return ComposerOfSkeleton(collector)
        }

        private fun createComposerOfHeader(): ComposerOfOneColumnText {
            val collector = CollectorOfHeader(
                currencyAmountLabel = carrier.currencyAmountLabel,
                currencyAmounts = carrier.currencyAmounts,
                formatter = amountFormatter,
                productGroupLabel = carrier.productGroupLabel,
                factory = factoryOfOneColumnTextEntity,
                horizontalPaddingEntity = horizontalPaddingEntity,
            )
            return ComposerOfOneColumnText(collector)
        }

        private fun createComposerOfProductRadioButton(
            receiver: InstanceReceiver,
        ): ComposerOfProductRadioButton {

            val converter = ConverterOfProductRadioButton(
                horizontalPaddingEntity = horizontalPaddingEntity,
                currencyFormatter = amountFormatter,
                formatterOfProductName = formatterOfProductName,
                factory = factoryOfOneColumnTextEntity,
            )
            return ComposerOfProductRadioButton(converter, receiver)
        }

        private fun createComposerOfExchangeRate(): ComposerOfExchangeRate {
            val converter = ConverterOfExchangeRate(
                appModel = appModel,
                weakResources = weakResources,
                currencyAmounts = carrier.currencyAmounts,
                formatter = exchangeRateFormatter,
                twoColumnIdOfExchangeRate = idRegistry.twoColumnIdOfExchangeRate,
                horizontalPaddingEntity = horizontalPaddingEntity,
            )
            return ComposerOfExchangeRate(converter)
        }

        private fun createComposerOfInstallmentChipsComponent(
            receiver: InstanceReceiver,
        ): ComposerOfInstallmentChipsComponent {
            val collection: Collection<InstallmentOption> = listOf(
                InstallmentOption.IN_FULL,
                InstallmentOption.IN_INSTALLMENTS,
            )
            val default: InstallmentOption = collection.first()
            val collector = CollectorOfInstallmentChipsComponent(
                collection = collection,
                default = default,
                weakResources = weakResources,
                horizontalPaddingEntity = horizontalPaddingEntity,
            )
            return ComposerOfInstallmentChipsComponent(collector, receiver)
        }

        private fun createComposerOfEditableInstallment(
            receiver: InstanceReceiver,
        ): ComposerOfEditableInstallment {
            val converter = ConverterOfEditableInstallment(
                weakResources = weakResources,
                horizontalPaddingEntity = horizontalPaddingEntity,
                receiver = receiver,
                installmentFieldId = idRegistry.installmentFieldId,
            )
            return ComposerOfEditableInstallment(converter)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
