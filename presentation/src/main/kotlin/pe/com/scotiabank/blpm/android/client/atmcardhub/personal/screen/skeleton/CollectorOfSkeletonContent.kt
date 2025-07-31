package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen.skeleton

import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.CollectorOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class CollectorOfSkeletonContent(
    private val paddingEntity: UiEntityOfPadding,
): CollectorOfSkeleton {

    override fun collect(): List<UiEntityOfSkeleton> {

        val entityForTop = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            isStretchedWidth = true,
            height = R.dimen.hub_card_skeleton_height,
        )

        return listOf(entityForTop)
    }
}
