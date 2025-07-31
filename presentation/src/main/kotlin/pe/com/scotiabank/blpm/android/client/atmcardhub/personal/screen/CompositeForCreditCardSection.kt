package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.ComposerOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText

class CompositeForCreditCardSection(
    private val composerOfTitle: ComposerOfOneColumnText,
    private val composerOfSkeletonTitle: ComposerOfSkeleton,
    private val skeletonComposerForBodyLoading: ComposerOfSkeleton,
    private val composerOfErrorCard: ComposerOfCard,
    val composerOfHubCreditCard: ComposerOfHubCreditCard,
): UiStateHolder  {

    private var isCreditCardsReload: Boolean = false

    private val isTitleVisible: Boolean
        get() = isCreditCardsReload || isErrorVisible xor isSuccessVisible

    private val isSkeletonTitleVisible: Boolean
        get() = isTitleVisible.not() && isErrorVisible.not() && isLoadingVisible

    override var currentState: UiState = UiState.BLANK
        set(newValue) {
            val previousValue: UiState = field
            isCreditCardsReload = (UiState.ERROR == previousValue && UiState.LOADING == newValue)
            field = newValue
        }

    fun compose(): List<UiCompound<*>> {

        val skeletonTitleCompound = composerOfSkeletonTitle.composeUiData(
            visibilitySupplier = Supplier(::isSkeletonTitleVisible),
        )

        val titleCompound = composerOfTitle.composeUiData(
            visibilitySupplier = Supplier(::isTitleVisible),
        )

        val skeletonCompoundForBodyLoading = skeletonComposerForBodyLoading.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible)
        )

        val errorCardCompound = composerOfErrorCard.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible)
        )

        val hubCreditCardListCompound = composerOfHubCreditCard.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )

        return listOf(
            skeletonTitleCompound,
            titleCompound,
            skeletonCompoundForBodyLoading,
            errorCardCompound,
            hubCreditCardListCompound,
        )
    }
}
