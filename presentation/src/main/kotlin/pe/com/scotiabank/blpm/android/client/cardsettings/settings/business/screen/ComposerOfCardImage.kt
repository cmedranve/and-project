package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.AdapterFactoryOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.UiEntityOfOneColumnImage

class ComposerOfCardImage (
    private val converter: ConverterOfCardImage,
): CardImageService {

    private val entities: MutableList<UiEntityOfOneColumnImage> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfOneColumnImage> {

        val adapterFactory = AdapterFactoryOfOneColumnImage()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun clearThenAdd(isLocked: Boolean) {
        entities.clear()
        val newEntity: UiEntityOfOneColumnImage = converter.toUiEntity(isLocked)
        entities.add(newEntity)
    }
}