package pe.com.scotiabank.blpm.android.ui.list.items.image.qr

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfQr(
    val paddingEntity: UiEntityOfPadding,
    val imageEntity: UiEntityOfImage,
    val rawQr: String,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfQr>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfQr
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && imageEntity.isHoldingTheSameContentAs(other.imageEntity)
            && rawQr.contentEquals(other.rawQr)
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        imageEntity.resetChangingState()
    }
}
