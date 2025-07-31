package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.AdapterFactoryOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard

class CardComposerOfAtmCard(private val converter: CardConverterOfAtmCard) : AtmCardService {

    private val entities: MutableList<UiEntityOfCard<Any>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCard<Any>> {

        val adapterFactory: AdapterFactoryOfCard<Any> = AdapterFactoryOfCard()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun addAtmCard(card: Card) {
        val entity: UiEntityOfCard<Any> = converter.toUiEntity(card)
        entities.add(entity)
    }
}
