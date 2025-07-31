package pe.com.scotiabank.blpm.android.ui.list.items.image

import android.view.View
import android.widget.ImageView
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfImage(
    val scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    val importantForAccessibility: Int = View.IMPORTANT_FOR_ACCESSIBILITY_NO,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    override val id: Long = randomLong(),
    changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfImage>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfImage
    ): Boolean = isUnmodified
            && scaleType == other.scaleType
            && importantForAccessibility == other.importantForAccessibility
}
