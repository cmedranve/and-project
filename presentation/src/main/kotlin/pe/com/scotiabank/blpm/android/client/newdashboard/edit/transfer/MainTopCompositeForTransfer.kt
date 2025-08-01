package pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer

import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.IdentifiableEditText
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CollectorOfHorizontalCurrencyAmount
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.ComposerOfHorizontalCurrencyAmount
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.CollectorOfEditableName
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.ComposerOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.util.concurrent.ConcurrentHashMap

class MainTopCompositeForTransfer private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfEditableName: ComposerOfEditText<IdentifiableEditText>,
    private val composerOfHorizontalCurrencyAmount: ComposerOfHorizontalCurrencyAmount,
): DispatcherProvider by dispatcherProvider {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    val editableNameEntity: UiEntityOfEditText<IdentifiableEditText>? by composerOfEditableName::entity

    val selectedCurrencyEntity: UiEntityOfChip<Currency>? by composerOfHorizontalCurrencyAmount::selectedCurrencyEntity
    val selectedCurrency: Currency?
        get() = selectedCurrencyEntity?.data

    val amountEntity: UiEntityOfEditText<IdentifiableEditText>?
        get() = composerOfHorizontalCurrencyAmount.amountEntity
    val amountText: CharSequence?
        get() = amountEntity?.text

    private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = R.dimen.canvascore_margin_16,
            right = R.dimen.canvascore_margin_16,
        )
    }

    private val paddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_12,
            bottom = R.dimen.canvascore_margin_12,
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
        )
    }

    private val paddingEntityOfCurrency: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            right = horizontalPaddingEntity.right,
        )
    }

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val editableNameCompound = composerOfEditableName.composeUiData(
            paddingEntity = horizontalPaddingEntity,
        )

        val horizontalCurrencyAmountCompound = composerOfHorizontalCurrencyAmount.composeUiData(
            paddingEntityOfHorizontal = paddingEntity,
            paddingEntityOfCurrency = paddingEntityOfCurrency,
        )

        return listOf(
            editableNameCompound,
            horizontalCurrencyAmountCompound,
        )
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val collectorOfEditableName: CollectorOfEditableName,
        private val collectorOfCurrencyAmount: CollectorOfHorizontalCurrencyAmount,
    ) {

        fun create(receiver: InstanceReceiver) = MainTopCompositeForTransfer(
            dispatcherProvider = dispatcherProvider,
            composerOfEditableName = createComposerOfEditableName(receiver),
            composerOfHorizontalCurrencyAmount = createComposerOfHorizontalCurrencyAmount(receiver),
        )

        private fun createComposerOfEditableName(
            receiver: InstanceReceiver,
        ) = ComposerOfEditText(
            collector = collectorOfEditableName,
            receiver = receiver,
        )

        private fun createComposerOfHorizontalCurrencyAmount(
            receiver: InstanceReceiver,
        ) = ComposerOfHorizontalCurrencyAmount(
            collector = collectorOfCurrencyAmount,
            receiver = receiver,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}