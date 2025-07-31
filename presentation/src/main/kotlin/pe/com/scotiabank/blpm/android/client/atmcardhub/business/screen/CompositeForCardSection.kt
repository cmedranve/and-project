package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText

class CompositeForCardSection(
    private val composerOfTitle: ComposerOfOneColumnText,
    private val composerOfAnyCard: ComposerOfAnyCard,
    override var currentState: UiState = UiState.BLANK,
): UiStateHolder, CardService by composerOfAnyCard {

    fun compose(): List<UiCompound<*>> {
        val titleCompound = composerOfTitle.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        val cardsCompound = composerOfAnyCard.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )

        return listOf(
            titleCompound,
            cardsCompound,
        )
    }
}
