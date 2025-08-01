package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.transfer

import androidx.core.util.Predicate
import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.quantitytext.TextComposerForQuantity
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.CompositeForOperationTypeSection
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.HolderOfCheckBoxController
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.HolderOfImmediateAvailability
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.ComposerOfAlertBanner
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading.ComposerOfCanvasButtonLoading
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText

class CompositeForTransferSection(
    private val paddingEntity: UiEntityOfPadding,
    private val imagePaddingEntityForDisabledOrErrorState: UiEntityOfPadding,
    private val imageComposerForDisabledOrErrorState: ComposerOfOneColumnImage,
    private val textComposerForDisabledState: ComposerOfOneColumnText,
    private val textComposerForErrorState: ComposerOfOneColumnText,
    private val buttonComposerForErrorState: ComposerOfCanvasButtonLoading,
    private val textComposerForEmptyState: ComposerOfOneColumnText,
    private val textComposerForQuantity: TextComposerForQuantity,
    private val composerOfUnavailableImmediate: ComposerOfAlertBanner,
    private val composerOfCheckableTransfer: ComposerOfCheckableTransfer,
    override val visibilityPredicate: Predicate<FrequentOperationType>,
    override val frequentOperationType: FrequentOperationType = FrequentOperationType.TRANSFER,
    override var currentState: UiState = UiState.BLANK,
): HolderOfCheckBoxController by composerOfCheckableTransfer,
    HolderOfImmediateAvailability,
    CompositeForOperationTypeSection
{

    override var isImmediateAvailable: Boolean = true
    private val isUnavailableImmediateToBeVisible: Boolean
        get() = isSuccessVisible && isImmediateAvailable.not()

    override val quantity: Int
        get() = composerOfCheckableTransfer.quantity

    fun compose(): List<UiCompound<*>> {

        val imageCompoundForDisabledOrErrorState = imageComposerForDisabledOrErrorState.composeUiData(
            paddingEntity = imagePaddingEntityForDisabledOrErrorState,
            visibilitySupplier = Supplier(::isDisabledOrErrorVisible),
        )
        
        val textCompoundForDisabledState = textComposerForDisabledState.composeUiData(
            visibilitySupplier = Supplier(::isDisabledVisible),
        )

        val textCompoundForErrorState = textComposerForErrorState.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible),
        )

        val buttonCompoundForErrorState = buttonComposerForErrorState.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible),
        )

        val textCompoundForEmptyState = textComposerForEmptyState.composeUiData(
            visibilitySupplier = Supplier(::isEmptyVisible),
        )

        val unavailableImmediateCompound = composerOfUnavailableImmediate.composeUiData(
            paddingEntity = paddingEntity,
            callback = null,
            visibilitySupplier = Supplier(::isUnavailableImmediateToBeVisible),
        )

        val textCompoundForSuccessState = textComposerForQuantity.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        val checkableOperationCompound = composerOfCheckableTransfer.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        return listOf(
            imageCompoundForDisabledOrErrorState,
            textCompoundForDisabledState,
            textCompoundForErrorState,
            buttonCompoundForErrorState,
            textCompoundForEmptyState,
            unavailableImmediateCompound,
            textCompoundForSuccessState,
            checkableOperationCompound,
        )
    }

    override fun add(frequentOperation: FrequentOperationModel) {
        composerOfCheckableTransfer.add(frequentOperation)
        textComposerForQuantity.updateTextWith(quantity)
        currentState = UiState.from(quantity)
    }

    override fun edit(frequentOperation: FrequentOperationModel) {
        composerOfCheckableTransfer.edit(frequentOperation)
    }

    override fun remove(frequentOperation: FrequentOperationModel) {
        composerOfCheckableTransfer.remove(frequentOperation)
        textComposerForQuantity.updateTextWith(quantity)
        currentState = UiState.from(quantity)
    }

    override fun clear() {
        composerOfCheckableTransfer.clear()
        currentState = UiState.from(quantity)
    }

    override fun addForCanvasButtonLoading(
        id: Long,
        isEnabled: Boolean,
        text: String,
        data: Any?,
        state: Int
    ) {
        buttonComposerForErrorState.addForCanvasButtonLoading(id, isEnabled, text, data, state)
    }

    override fun editForCanvasButtonLoading(
        id: Long,
        isEnabled: Boolean,
        text: String,
        state: Int
    ) {
        buttonComposerForErrorState.editForCanvasButtonLoading(id, isEnabled, text, state)
    }

    override fun removeForCanvasButtonLoading(id: Long) {
        buttonComposerForErrorState.removeForCanvasButtonLoading(id)
    }
}