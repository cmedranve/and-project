package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.transfer

import android.view.Gravity
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.CollectorOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class SkeletonCollectorForTransferTitle(
    private val paddingEntity: UiEntityOfPadding,
): CollectorOfSkeleton {

    override fun collect(): List<UiEntityOfSkeleton> {

        val entityForLabel = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            isStretchedWidth = false,
            width = R.dimen.canvascore_width_150,
            height = R.dimen.canvascore_skeleton_h3_height,
            gravity = Gravity.START,
        )

        return listOf(entityForLabel)
    }
}