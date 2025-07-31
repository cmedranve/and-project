package pe.com.scotiabank.blpm.android.ui.list.items.image.qr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQrItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiBinderOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfQr {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfQr, ViewQrItemBinding>) {
        val entity: UiEntityOfQr = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfQr, binding: ViewQrItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindQr(entity, binding)
        UiBinderOfImage.bind(entity.imageEntity, binding.ivImage)
    }

    @JvmStatic
    private fun bindQr(
        entity: UiEntityOfQr,
        binding: ViewQrItemBinding
    ) {
        val bitmap: Bitmap = toBitmap(entity.rawQr)
        binding.ivImage.setImageBitmap(bitmap)
    }

    @JvmStatic
    private fun toBitmap(rawQr: String): Bitmap {
        val decodedBytes: ByteArray = Base64.decode(rawQr, Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
