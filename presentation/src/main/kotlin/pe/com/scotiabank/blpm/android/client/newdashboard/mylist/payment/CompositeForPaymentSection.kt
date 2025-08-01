package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import androidx.core.util.Predicate
import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.CompositeForOperationTypeSection
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.HolderOfCheckBoxController
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading.ComposerOfCanvasButtonLoading
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText

class CompositeForPaymentSection(
    private val imagePaddingEntityForDisabledOrErrorState: UiEntityOfPadding,
    private val imageComposerForDisabledOrErrorState: ComposerOfOneColumnImage,
    private val textComposerForDisabledState: ComposerOfOneColumnText,
    private val textComposerForErrorState: ComposerOfOneColumnText,
    private val buttonComposerForErrorState: ComposerOfCanvasButtonLoading,
    private val textComposerForEmptyState: ComposerOfOneColumnText,
    private val textButtonComposerForEmptyState: TextButtonComposerForEmptyState,
    private val labelButtonComposerForSuccessState: LabelButtonComposerForSuccessState,
    private val composerOfCheckablePayment: ComposerOfCheckablePayment,
    private val composerOfSuccessfulPayment: ComposerOfSuccessfulPayment,
    override val visibilityPredicate: Predicate<FrequentOperationType>,
    override val frequentOperationType: FrequentOperationType = FrequentOperationType.PAYMENT,
    override var currentState: UiState = UiState.BLANK,
): HolderOfCheckBoxController by composerOfCheckablePayment,
    CompositeForOperationTypeSection
{

    override val quantity: Int
        get() = composerOfCheckablePayment.quantity + composerOfSuccessfulPayment.quantity

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

        val labelButtonComposerForEmptyState = textButtonComposerForEmptyState.composeUiData(
            visibilitySupplier = Supplier(::isEmptyVisible)
        )

        val labelButtonCompoundForSuccessState = labelButtonComposerForSuccessState.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        val checkableOperationCompound = composerOfCheckablePayment.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        val successfulOperationCompound = composerOfSuccessfulPayment.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        return listOf(
            imageCompoundForDisabledOrErrorState,
            textCompoundForDisabledState,
            textCompoundForErrorState,
            buttonCompoundForErrorState,
            textCompoundForEmptyState,
            labelButtonComposerForEmptyState,
            labelButtonCompoundForSuccessState,
            checkableOperationCompound,
            successfulOperationCompound,
        )
    }

    override fun add(frequentOperation: FrequentOperationModel) {

        if (frequentOperation.isSuccess) {
            composerOfSuccessfulPayment.add(frequentOperation)
            labelButtonComposerForSuccessState.updateTextWith(quantity)
            currentState = UiState.from(quantity)
            return
        }

        composerOfCheckablePayment.add(frequentOperation)
        labelButtonComposerForSuccessState.updateTextWith(quantity)
        currentState = UiState.from(quantity)
    }

    override fun edit(frequentOperation: FrequentOperationModel) {

        if (frequentOperation.isSuccess) {
            composerOfSuccessfulPayment.edit(frequentOperation)
            return
        }

        composerOfCheckablePayment.edit(frequentOperation)
    }

    override fun remove(frequentOperation: FrequentOperationModel) {

        if (frequentOperation.isSuccess) {
            composerOfSuccessfulPayment.remove(frequentOperation)
            labelButtonComposerForSuccessState.updateTextWith(quantity)
            currentState = UiState.from(quantity)
            return
        }

        composerOfCheckablePayment.remove(frequentOperation)
        labelButtonComposerForSuccessState.updateTextWith(quantity)
        currentState = UiState.from(quantity)
    }

    override fun clear() {
        composerOfSuccessfulPayment.clear()
        composerOfCheckablePayment.clear()
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