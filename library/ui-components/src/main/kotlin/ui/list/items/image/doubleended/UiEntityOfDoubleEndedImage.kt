package pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.BIAS_AT_ZERO
import kotlin.properties.Delegates

class UiEntityOfDoubleEndedImage<D: Any>(
    @DrawableRes leftDrawableId: Int = ResourcesCompat.ID_NULL,
    val verticalBiasOfLeftImage: Float = BIAS_AT_ZERO,
    val paddingEntityOfLeftImage: UiEntityOfPadding,
    val leftImageEntity: UiEntityOfImage,
    @DrawableRes rightDrawableId: Int = ResourcesCompat.ID_NULL,
    val verticalBiasOfRightImage: Float = BIAS_AT_ZERO,
    val paddingEntityOfRightImage: UiEntityOfPadding,
    val rightImageEntity: UiEntityOfImage,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val centerRecyclerEntity: UiEntityOfRecycler,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfDoubleEndedImage<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var leftDrawableId: Int by Delegates.observable(
        leftDrawableId,
        ::onChangeOfNonEntityProperty
    )

    var rightDrawableId: Int by Delegates.observable(
        rightDrawableId,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfDoubleEndedImage<D>
    ): Boolean = isUnmodified
            && leftDrawableId == other.leftDrawableId
            && verticalBiasOfLeftImage == other.verticalBiasOfLeftImage
            && paddingEntityOfLeftImage.isHoldingTheSameContentAs(other.paddingEntityOfLeftImage)
            && leftImageEntity.isHoldingTheSameContentAs(other.leftImageEntity)
            && rightDrawableId == other.rightDrawableId
            && verticalBiasOfRightImage == other.verticalBiasOfRightImage
            && paddingEntityOfRightImage.isHoldingTheSameContentAs(other.paddingEntityOfRightImage)
            && rightImageEntity.isHoldingTheSameContentAs(other.rightImageEntity)
            && expectedFlexGrow == other.expectedFlexGrow
            && centerRecyclerEntity.isHoldingTheSameContentAs(other.centerRecyclerEntity)

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntityOfLeftImage.resetChangingState()
        paddingEntityOfRightImage.resetChangingState()
    }
}
