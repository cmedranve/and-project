package pe.com.scotiabank.blpm.android.ui.list.items.image.pdf

import android.graphics.pdf.PdfRenderer
import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfPdfPageImage(private val converter: ConverterForPdfPageImage) : PdfPageService {

    private val entities: MutableList<UiEntityOfPdfPageImage> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfPdfPageImage> {

        val adapterFactory = AdapterFactoryOfPdfPageImage()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun add(renderer: PdfRenderer) {
        val newEntities: List<UiEntityOfPdfPageImage> = converter.toUiEntitiesOfPdfImageImage(
            renderer = renderer,
        )
        entities.clear()
        entities.addAll(newEntities)
    }
}
