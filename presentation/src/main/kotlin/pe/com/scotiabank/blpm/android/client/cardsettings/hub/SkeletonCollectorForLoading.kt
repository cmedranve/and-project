package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.CollectorOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class SkeletonCollectorForLoading(
    private val paddingEntity: UiEntityOfPadding,
) : CollectorOfSkeleton {

    override fun collect(): List<UiEntityOfSkeleton> {

        val entityForLabel = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            height = R.dimen.canvascore_margin_24,
        )

        val entityForAtmCard1 = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            height = R.dimen.canvascore_margin_48,
        )

        val entityForAtmCard2 = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            height = R.dimen.canvascore_margin_48,
        )

        val entityForPaymentCategory = UiEntityOfSkeleton(
            paddingEntity = paddingEntity,
            height = R.dimen.canvascore_margin_84,
        )

        return listOf(
            entityForLabel,
            entityForAtmCard1,
            entityForAtmCard2,
            entityForPaymentCategory,
        )
    }
}
