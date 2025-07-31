package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText

class ComposerOfLabel(private val converter: ConverterOfLabel) : AtmCardGroupService {

    private val entities: MutableList<UiEntityOfOneColumnText> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfOneColumnText> {
        val adapterFactory = AdapterFactoryOfOneColumnText()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun addAtmCardGroup(group: AtmCardGroup) {
        val entity: UiEntityOfOneColumnText = converter.toUiEntity(group)
        entities.add(entity)
    }
}
