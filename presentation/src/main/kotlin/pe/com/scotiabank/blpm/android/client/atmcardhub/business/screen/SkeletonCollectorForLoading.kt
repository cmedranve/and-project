package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.CollectorOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class SkeletonCollectorForLoading(private val paddingEntity: UiEntityOfPadding): CollectorOfSkeleton {

    override fun collect(): List<UiEntityOfSkeleton> {

        val entityForTitle = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            isStretchedWidth = false,
            width = R.dimen.hub_title_skeleton_width,
            height = R.dimen.hub_title_skeleton_height,
            gravity = Gravity.START,
        )

        val entityForTop = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            isStretchedWidth = true,
            height = R.dimen.hub_card_skeleton_height,
        )

        val entityForBottom = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            isStretchedWidth = true,
            height = R.dimen.hub_card_skeleton_height,
        )

        return listOf(entityForTitle, entityForTop, entityForBottom)
    }
}
