package pe.com.scotiabank.blpm.android.ui.list.items.loading

import androidx.core.util.Supplier
import com.scotiabank.canvascore.views.CanvasLoadingIndicator
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

class ComposerOfLoading(private val paddingEntity: UiEntityOfPadding) {

    private val itemEntities: MutableList<UiEntityOfLoadingIndicator> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfLoadingIndicator> {

        val adapterFactory = AdapterFactoryOfLoadingIndicator()
        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    fun add(id: Long, loadingType: Int = CanvasLoadingIndicator.BRAND_RED) {
        val entity = UiEntityOfLoadingIndicator(
            paddingEntity = paddingEntity,
            loadingType = loadingType,
            id = id,
        )
        itemEntities.add(entity)
    }
}
