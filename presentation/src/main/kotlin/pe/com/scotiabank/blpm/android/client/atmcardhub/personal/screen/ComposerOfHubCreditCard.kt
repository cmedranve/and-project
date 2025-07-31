package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.AdapterFactoryOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard

class ComposerOfHubCreditCard(private val converter: ConverterOfHubCreditCard): CardHubService {

    var cardEntities: MutableList<UiEntityOfCard<Any>> = mutableListOf()
        private set
    private var productModels: MutableList<NewProductModel> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCard<Any>> {

        val adapterFactory: AdapterFactoryOfCard<Any> = AdapterFactoryOfCard()
        return UiCompound(cardEntities, adapterFactory, visibilitySupplier)
    }

    override fun add(newProductModel: NewProductModel) {
        val entity = converter.toUiEntity(newProductModel)
        cardEntities.add(entity)
        productModels.add(newProductModel)
    }

    override fun fetchAll(): List<NewProductModel> {
        return productModels
    }

    override fun clear() {
        cardEntities.clear()
    }

}
