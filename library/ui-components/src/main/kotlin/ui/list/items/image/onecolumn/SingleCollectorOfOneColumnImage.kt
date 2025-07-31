package pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn

import androidx.annotation.DrawableRes
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class SingleCollectorOfOneColumnImage(
    @DrawableRes private val drawableRes: Int,
    private val entityOfColumn: UiEntityOfImage = UiEntityOfImage(),
    private val data: Any? = null,
): CollectorOfOneColumnImage {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?,
    ): List<UiEntityOfOneColumnImage> {

        val entity = UiEntityOfOneColumnImage(
            paddingEntity = paddingEntity,
            entityOfColumn = entityOfColumn,
            drawableRes = drawableRes,
        )
        return listOf(entity)
    }
}
