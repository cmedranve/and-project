package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

class GroupComposite(
    uiStateHolder: UiStateHolder,
    private val composerOfLabel: ComposerOfLabel,
    private val cardComposerOfAtmCard: CardComposerOfAtmCard,
) : UiStateHolder by uiStateHolder, AtmCardGroupService {

    fun composeItself(): List<UiCompound<*>> {

        val labelCompound = composerOfLabel.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        val cardCompoundOfAtmCard = cardComposerOfAtmCard.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        return listOf(
            labelCompound,
            cardCompoundOfAtmCard,
        )
    }

    override fun addAtmCardGroup(group: AtmCardGroup) {
        composerOfLabel.addAtmCardGroup(group)
        group.cards.forEach(cardComposerOfAtmCard::addAtmCard)
    }
}
