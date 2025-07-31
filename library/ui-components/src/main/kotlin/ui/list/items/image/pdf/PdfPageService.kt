package pe.com.scotiabank.blpm.android.ui.list.items.image.pdf

import android.graphics.pdf.PdfRenderer

interface PdfPageService {

    fun add(renderer: PdfRenderer)
}
