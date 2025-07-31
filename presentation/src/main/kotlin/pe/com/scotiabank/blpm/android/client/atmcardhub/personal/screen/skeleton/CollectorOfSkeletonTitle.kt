package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen.skeleton

import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.CollectorOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class CollectorOfSkeletonTitle(private val paddingEntity: UiEntityOfPadding): CollectorOfSkeleton {

    override fun collect(): List<UiEntityOfSkeleton> {

        val entity = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            isStretchedWidth = false,
            width = R.dimen.hub_title_skeleton_width,
            height = R.dimen.hub_title_skeleton_height,
            gravity = Gravity.START,
        )

        return listOf(entity)
    }
}
