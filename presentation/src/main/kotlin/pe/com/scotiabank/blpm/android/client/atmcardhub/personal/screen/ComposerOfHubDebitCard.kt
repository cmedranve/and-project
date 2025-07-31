package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.debitcard.DebitCard
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.AdapterFactoryOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard

class ComposerOfHubDebitCard(private val converter: ConverterOfHubDebitCard) : CardDebitHubService {

    var cardEntities: MutableList<UiEntityOfCard<Any>> = mutableListOf()
        private set

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCard<Any>> {

        val adapterFactory: AdapterFactoryOfCard<Any> = AdapterFactoryOfCard()
        return UiCompound(cardEntities, adapterFactory, visibilitySupplier)
    }

    override fun add(debitCard: DebitCard) {
        val entity = converter.toUiEntity(debitCard)
        cardEntities.add(entity)
    }

    override fun clear() {
        cardEntities.clear()
    }

}
