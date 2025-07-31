package pe.com.scotiabank.blpm.android.ui.list.items.image.qr

import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class ConverterForQr(
    private val paddingEntity: UiEntityOfPadding,
    private val imageEntity: UiEntityOfImage,
) {

    fun toUiEntityOfQr(rawQr: String): UiEntityOfQr = UiEntityOfQr(
        paddingEntity = paddingEntity,
        imageEntity = imageEntity,
        rawQr = rawQr,
    )
}
