package pe.com.scotiabank.blpm.android.ui.list.items.image.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import pe.com.scotiabank.blpm.android.ui.databinding.ViewPdfPageImageItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiBinderOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfPdfPageImage {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfPdfPageImage, ViewPdfPageImageItemBinding>) {
        val entity: UiEntityOfPdfPageImage = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfPdfPageImage, binding: ViewPdfPageImageItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindPdfBitmap(entity, binding)
        UiBinderOfImage.bind(entity.imageEntity, binding.ivPage)
    }

    @JvmStatic
    private fun bindPdfBitmap(
        entity: UiEntityOfPdfPageImage,
        binding: ViewPdfPageImageItemBinding
    ) {
        binding.ivPage.post {
            val page: PdfRenderer.Page = entity.renderer.openPage(entity.pageIndex) ?: return@post
            val bitmap: Bitmap = page.renderAndClose(binding.ivPage.width)
            binding.ivPage.setImageBitmap(bitmap)
        }
    }
}
