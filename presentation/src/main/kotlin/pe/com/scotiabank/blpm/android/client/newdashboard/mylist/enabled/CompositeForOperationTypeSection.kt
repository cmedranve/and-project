package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import androidx.core.util.Predicate
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading.CanvasButtonLoadingController

interface CompositeForOperationTypeSection:
    HolderOfCheckBoxController,
    FrequentOperationService,
    CanvasButtonLoadingController,
    UiStateHolder
{

    val visibilityPredicate: Predicate<FrequentOperationType>
    val frequentOperationType: FrequentOperationType

    override val isDisabledVisible: Boolean
        get() = UiState.DISABLED == currentState && visibilityPredicate.test(frequentOperationType)
    override val isLoadingVisible: Boolean
        get() = UiState.LOADING == currentState && visibilityPredicate.test(frequentOperationType)
    override val isErrorVisible: Boolean
        get() = UiState.ERROR == currentState && visibilityPredicate.test(frequentOperationType)
    override val isEmptyVisible: Boolean
        get() = UiState.EMPTY == currentState && visibilityPredicate.test(frequentOperationType)
    override val isSuccessVisible: Boolean
        get() = UiState.SUCCESS == currentState && visibilityPredicate.test(frequentOperationType)

    val isDisabledOrErrorVisible: Boolean
        get() = isDisabledVisible xor isErrorVisible
}
