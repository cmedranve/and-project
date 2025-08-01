package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.sectionloading

import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationRendering
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationUtil
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.AdapterFactoryOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.CollectorOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended.CollectorOfDoubleEndedSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended.UiEntityOfDoubleEndedSkeleton

class SkeletonCollectorForSection(
    private val dividerPositions: List<Int>,
    private val paddingEntity: UiEntityOfPadding,
    private val paddingOfLeftSkeleton: UiEntityOfPadding,
    private val collectorOfHorizontalSkeletonList: CollectorOfRecycler,
) : CollectorOfDoubleEndedSkeleton {

    private val leftSkeletonEntity: UiEntityOfSkeleton by lazy {
        UiEntityOfSkeleton(
            paddingEntity = paddingOfLeftSkeleton,
            isStretchedWidth = false,
            width = R.dimen.canvascore_width_24,
            height = R.dimen.canvascore_height_24,
        )
    }

    private val paddingEntityOfCenterRecycler: UiEntityOfPadding by lazy {
        UiEntityOfPadding(right = paddingEntity.right, bottom = paddingEntity.bottom)
    }

    private val emptySkeletonEntity: UiEntityOfSkeleton by lazy {
        UiEntityOfSkeleton(
            paddingEntity = UiEntityOfPadding(),
            isStretchedWidth = false,
            width = R.dimen.canvascore_margin_0,
            height = R.dimen.canvascore_margin_0,
        )
    }

    override fun collect(): List<UiEntityOfDoubleEndedSkeleton> {

        val entity1 = createDoubleEndedSkeletonEntity()
        val entity2 = createDoubleEndedSkeletonEntity()
        val entity3 = createDoubleEndedSkeletonEntity()

        return listOf(entity1, entity2, entity3)
    }

    private fun createDoubleEndedSkeletonEntity(): UiEntityOfDoubleEndedSkeleton {

        val compound = UiCompound(
            uiEntities = collectorOfHorizontalSkeletonList.collect(),
            factoryOfPortableAdapter = AdapterFactoryOfRecycler(),
        )
        val compounds: List<UiCompound<*>> = listOf(compound)

        val centerRecyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfCenterRecycler,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
            decorationCompounds = createDecorationCompounds(),
        )
        compounds.associateByTo(destination = centerRecyclerEntity.compoundsById, keySelector = ::byId)

        return UiEntityOfDoubleEndedSkeleton(
            leftSkeletonEntity = leftSkeletonEntity,
            rightSkeletonEntity = emptySkeletonEntity,
            centerRecyclerEntity = centerRecyclerEntity,
        )
    }

    private fun createDecorationCompounds(): List<DecorationCompound> {
        val decorationCompound = DecorationCompound(
            positions = dividerPositions,
            rendering = DecorationRendering(DecorationUtil::addDividerAboveEachItem),
        )
        return listOf(decorationCompound)
    }
}
