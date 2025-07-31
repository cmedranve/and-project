package pe.com.scotiabank.blpm.android.ui.list.items.image.pdf

import android.graphics.pdf.PdfRenderer
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class ConverterForPdfPageImage(
    private val paddingEntity: UiEntityOfPadding,
    private val imageEntity: UiEntityOfImage,
    private val receiver: InstanceReceiver,
) {

    fun toUiEntitiesOfPdfImageImage(renderer: PdfRenderer): List<UiEntityOfPdfPageImage> {
        val pageRange: IntRange = INDEX_OF_FIRST_PAGE until renderer.pageCount
        return pageRange.map { pageIndex ->
            UiEntityOfPdfPageImage(paddingEntity, imageEntity, pageIndex, renderer, receiver)
        }
    }

    companion object {

        private val INDEX_OF_FIRST_PAGE: Int
            get() = 0
    }
}
