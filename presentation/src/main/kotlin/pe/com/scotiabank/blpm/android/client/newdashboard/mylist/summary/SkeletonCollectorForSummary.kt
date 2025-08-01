package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary

import android.view.Gravity
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.CollectorOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfFlexboxLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.AdapterFactoryOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton

class SkeletonCollectorForSummary(
    private val paddingEntity: UiEntityOfPadding,
): CollectorOfRecycler {

    private val paddingEntityForTransferType: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            right = R.dimen.canvascore_margin_8,
        )
    }

    private val paddingEntityForPaymentType: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = R.dimen.canvascore_margin_8,
        )
    }

    override fun collect(): List<UiEntityOfRecycler> {
        val entityForTypes = createEntityForTypes()
        return listOf(entityForTypes)
    }

    private fun createEntityForTypes(): UiEntityOfRecycler {

        val entityForTransferType = UiEntityOfSkeleton(
            paddingEntity = paddingEntityForTransferType,
            isStretchedWidth = false,
            width = R.dimen.canvascore_button_min_width,
            height = R.dimen.canvascore_skeleton_h2_height,
            gravity = Gravity.START,
        )

        val entityForPaymentType = UiEntityOfSkeleton(
            paddingEntity = paddingEntityForPaymentType,
            isStretchedWidth = false,
            width = R.dimen.canvascore_margin_84,
            height = R.dimen.canvascore_skeleton_h2_height,
            gravity = Gravity.START,
        )

        val compound = UiCompound(
            uiEntities = listOf(entityForTransferType, entityForPaymentType),
            factoryOfPortableAdapter = AdapterFactoryOfSkeleton(),
        )
        val compounds: List<UiCompound<*>> = listOf(compound)

        val entity = UiEntityOfRecycler(
            paddingEntity = paddingEntity,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfFlexboxLayoutManager(),
        )
        compounds.associateByTo(destination = entity.compoundsById, keySelector = ::byId)

        return entity
    }
}