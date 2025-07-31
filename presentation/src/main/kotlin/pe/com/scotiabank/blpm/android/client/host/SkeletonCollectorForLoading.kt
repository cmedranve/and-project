package pe.com.scotiabank.blpm.android.client.host

import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.CollectorOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class SkeletonCollectorForLoading(
    private val paddingEntity: UiEntityOfPadding,
) : CollectorOfSkeleton {

    private val paddingEntityForLogo: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_26,
            bottom = R.dimen.canvascore_margin_2,
            left = paddingEntity.left,
            right = paddingEntity.right,
        )
    }

    private val paddingEntityAtTwentyFour: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_24,
            left = paddingEntity.left,
            right = paddingEntity.right,
        )
    }

    override fun collect(): List<UiEntityOfSkeleton> {

        val entityForLogo = UiEntityOfSkeleton(
            paddingEntity = paddingEntityForLogo,
            isStretchedWidth = false,
            width = R.dimen.canvascore_margin_30,
            height = R.dimen.canvascore_margin_30,
        )

        val entityForDropdownOrLeadingAvatar = UiEntityOfSkeleton(
            paddingEntity = paddingEntityAtTwentyFour,
            height = R.dimen.canvascore_margin_72,
        )

        val entityForEditText = UiEntityOfSkeleton(
            paddingEntity = paddingEntityAtTwentyFour,
            height = R.dimen.canvascore_margin_72,
        )

        val entityForButton = UiEntityOfSkeleton(
            paddingEntity = paddingEntityAtTwentyFour,
            height = R.dimen.canvascore_margin_72,
        )

        return listOf(
            entityForLogo,
            entityForDropdownOrLeadingAvatar,
            entityForEditText,
            entityForButton,
        )
    }
}