package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.CollectorOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class SkeletonCollectorForLoading(
    private val horizontalPaddingEntity: UiEntityOfPadding,
) : CollectorOfSkeleton {

    override fun collect(): List<UiEntityOfSkeleton> {

        val entityForTotalAmount = UiEntityOfSkeleton(
            paddingEntity = horizontalPaddingEntity,
            height = R.dimen.canvascore_margin_48,
        )

        val entityForPaymentMethod = UiEntityOfSkeleton(
            paddingEntity = horizontalPaddingEntity,
            height = R.dimen.canvascore_skeleton_body1_height,
        )

        val entityForProductRadioButton1 = UiEntityOfSkeleton(
            paddingEntity = horizontalPaddingEntity,
            height = R.dimen.canvascore_margin_72,
        )

        val entityForProductRadioButton2 = UiEntityOfSkeleton(
            paddingEntity = horizontalPaddingEntity,
            height = R.dimen.canvascore_margin_72,
        )

        return listOf(
            entityForTotalAmount,
            entityForPaymentMethod,
            entityForProductRadioButton1,
            entityForProductRadioButton2,
        )
    }
}
