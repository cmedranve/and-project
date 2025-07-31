package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.AdapterFactoryOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard

class ComposerOfAnyCard(private val converter: ConverterOfAnyCard): CardService {

    private val entities: MutableList<UiEntityOfCard<Any>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCard<Any>> {

        val adapterFactory: AdapterFactoryOfCard<Any> = AdapterFactoryOfCard()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun clearThenAdd(cards: List<AtmCardInfo>) {
        entities.clear()
        cards.mapTo(entities, converter::toUiEntity)
    }
}
