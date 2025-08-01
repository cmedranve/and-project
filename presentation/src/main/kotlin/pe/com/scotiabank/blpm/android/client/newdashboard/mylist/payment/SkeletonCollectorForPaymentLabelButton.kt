package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

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
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

class SkeletonCollectorForPaymentLabelButton(
    private val paddingEntity: UiEntityOfPadding,
): CollectorOfRecycler {

    private val paddingEntityForLabel: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            right = R.dimen.canvascore_margin_78,
        )
    }

    private val paddingEntityForAddTextButton: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = R.dimen.canvascore_margin_78,
        )
    }

    override fun collect(): List<UiEntityOfRecycler> {

        val entityForTypes = createEntityForTypes()

        return listOf(entityForTypes)
    }

    private fun createEntityForTypes(): UiEntityOfRecycler {

        val entityForLabel = UiEntityOfSkeleton(
            paddingEntity = paddingEntityForLabel,
            isStretchedWidth = true,
            height = R.dimen.canvascore_skeleton_h3_height,
            expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
        )

        val entityForAddTextButton = UiEntityOfSkeleton(
            paddingEntity = paddingEntityForAddTextButton,
            isStretchedWidth = false,
            width = R.dimen.canvascore_margin_84,
            height = R.dimen.canvascore_skeleton_h3_height,
            gravity = Gravity.END,
        )

        val compound = UiCompound(
            uiEntities = listOf(entityForLabel, entityForAddTextButton),
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