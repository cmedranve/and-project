package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.util.BIAS_AT_MIDDLE

class ConverterOfCardInfo(private val receiver: InstanceReceiver) {

    private val paddingEntityOfLeftImage: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_12,
            bottom = R.dimen.canvascore_margin_12,
            left = R.dimen.canvascore_margin_24,
            right = R.dimen.canvascore_margin_12,
        )
    }

    private val paddingEntityOfRightImage: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_28,
            bottom = R.dimen.canvascore_margin_28,
            left = R.dimen.canvascore_margin_12,
            right = R.dimen.canvascore_margin_24,
        )
    }

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    private val imageEntity: UiEntityOfImage by lazy {
        UiEntityOfImage(scaleType = ScaleType.CENTER_INSIDE)
    }

    fun toUiEntity(
        @DrawableRes cardIcon: Int,
        oneColumnTextEntities: List<UiEntityOfOneColumnText>,
        @DrawableRes rightDrawableId: Int,
        data: Any,
        isClickable: Boolean,
    ): UiEntityOfDoubleEndedImage<Any> {

        val oneColumnTextCompound = UiCompound(
            uiEntities = oneColumnTextEntities,
            factoryOfPortableAdapter = AdapterFactoryOfOneColumnText(),
        )

        val compounds: List<UiCompound<*>> = listOf(oneColumnTextCompound)

        val centerRecyclerEntity = UiEntityOfRecycler(
            paddingEntity = emptyPaddingEntity,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = centerRecyclerEntity.compoundsById, keySelector = ::byId)

        return UiEntityOfDoubleEndedImage(
            leftDrawableId = cardIcon,
            verticalBiasOfLeftImage = BIAS_AT_MIDDLE,
            paddingEntityOfLeftImage = paddingEntityOfLeftImage,
            leftImageEntity = imageEntity,
            rightDrawableId = rightDrawableId,
            verticalBiasOfRightImage = BIAS_AT_MIDDLE,
            paddingEntityOfRightImage = paddingEntityOfRightImage,
            rightImageEntity = imageEntity,
            centerRecyclerEntity = centerRecyclerEntity,
            receiver = if (isClickable) receiver else null,
            data = data,
        )
    }
}
