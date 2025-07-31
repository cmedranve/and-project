package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.debitcard.PendingCard
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.AdapterFactoryOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard

class ComposerOfHubPendingDebitCard(
    private val converter: ConverterOfHubPendingDebitCard,
    override var currentState: UiState = UiState.LOADING
) : CardPendingDebitHubService, UiStateHolder {

    var cardEntities: MutableList<UiEntityOfCard<Any>> = mutableListOf()
        private set
    private var pendingCards: MutableList<PendingCard> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCard<Any>> {

        val adapterFactory: AdapterFactoryOfCard<Any> = AdapterFactoryOfCard()
        return UiCompound(cardEntities, adapterFactory, visibilitySupplier)
    }

    override fun addPendingCard(pendingCard: PendingCard) {
        val entity = converter.toUiEntity(pendingCard)
        cardEntities.add(entity)
        pendingCards.add(pendingCard)
    }

    override fun fetchAll(): List<PendingCard> {
        return pendingCards
    }

    override fun removePendingCard() {
        cardEntities.clear()
    }

}
