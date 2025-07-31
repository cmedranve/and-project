package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.ComposerOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText

class CompositeForDebitCardSection(
    private val composerOfTitle: ComposerOfOneColumnText,
    private val skeletonComposerForBodyLoading: ComposerOfSkeleton,
    private val composerOfErrorCard: ComposerOfCard,
    val composerOfHubPendingDebitCard: ComposerOfHubPendingDebitCard,
    val composerOfHubDebitCard: ComposerOfHubDebitCard,
): UiStateHolder {

    private var isDebitCardsReload: Boolean = false

    private val isTitleVisible: Boolean
        get() = isDebitCardsReload || isErrorVisible xor isSuccessVisible

    override var currentState: UiState = UiState.BLANK
        set(newValue) {
            val previousValue: UiState = field
            isDebitCardsReload = (UiState.ERROR == previousValue && UiState.LOADING == newValue)
            field = newValue
        }

    fun compose(): List<UiCompound<*>> {

        val titleCompound = composerOfTitle.composeUiData(
            visibilitySupplier = Supplier(::isTitleVisible),
        )

        val skeletonCompoundForBodyLoading = skeletonComposerForBodyLoading.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible)
        )

        val errorCardCompound = composerOfErrorCard.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible)
        )

        val hubDebitCardListCompound = composerOfHubDebitCard.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )

        val hubPendingCardCompound = composerOfHubPendingDebitCard.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )

        return listOf(
            titleCompound,
            skeletonCompoundForBodyLoading,
            errorCardCompound,
            hubPendingCardCompound,
            hubDebitCardListCompound,
        )
    }
}
