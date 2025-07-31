package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub

import android.text.TextUtils.TruncateAt
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.util.Constant
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

class ConverterOfCardInfo(
    private val factory: FactoryOfOneColumnTextEntity,
    private val receiver: InstanceReceiver,
) {

    private val paddingEntityOfLeftImage: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_12,
            bottom = R.dimen.canvascore_margin_12,
            right = R.dimen.canvascore_margin_18,
        )
    }

    private val imageEntity: UiEntityOfImage by lazy {
        UiEntityOfImage(scaleType = ImageView.ScaleType.CENTER_INSIDE)
    }

    private val paddingEntityOfCardName: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_22,
            bottom = R.dimen.canvascore_margin_2,
        )
    }

    private val paddingEntityOfRightImage: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_28,
            bottom = R.dimen.canvascore_margin_28,
            left = R.dimen.canvascore_margin_18,
        )
    }

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    fun toUiEntity(
        @DrawableRes cardIcon: Int,
        cardName: String,
        cardSubtitleEntity: UiEntityOfOneColumnText,
        data: Any,
        isClickable: Boolean,
    ): UiEntityOfDoubleEndedImage<Any> {

        val cardNameEntity = createEntityOfCardName(cardName)

        val oneColumnTextCompound = UiCompound(
            uiEntities = listOf(cardNameEntity, cardSubtitleEntity),
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
            rightDrawableId = pickRightDrawableId(isClickable),
            paddingEntityOfRightImage = paddingEntityOfRightImage,
            rightImageEntity = imageEntity,
            centerRecyclerEntity = centerRecyclerEntity,
            receiver = if (isClickable) receiver else null,
            data = data,
        )
    }

    @DrawableRes
    private fun pickRightDrawableId(
        isClickable: Boolean,
    ): Int = if (isClickable) R.drawable.canvascore_icon_chevron_right_black else ResourcesCompat.ID_NULL

    private fun createEntityOfCardName(cardName: String): UiEntityOfOneColumnText = factory.create(
        paddingEntity = paddingEntityOfCardName,
        appearance = R.style.canvascore_style_body2,
        text = cardName,
        maxLines = Constant.ONE,
        whereToEllipsize = TruncateAt.END,
    )
}
