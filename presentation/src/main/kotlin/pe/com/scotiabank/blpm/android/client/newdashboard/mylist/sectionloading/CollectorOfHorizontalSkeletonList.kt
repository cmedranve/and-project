package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.sectionloading

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

class CollectorOfHorizontalSkeletonList : CollectorOfRecycler {

    private val paddingOfIncludedTitleItem: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_12,
            bottom = R.dimen.canvascore_margin_4,
        )
    }

    private val paddingForLabel: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_4,
            right = R.dimen.canvascore_margin_10,
        )
    }

    private val paddingForLeftItem: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            right = R.dimen.canvascore_margin_10,
        )
    }

    private val paddingForRightItem: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = R.dimen.canvascore_margin_10,
        )
    }

    private val paddingEntityOfRecycler: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_4,
            bottom = R.dimen.canvascore_margin_4,
        )
    }

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    override fun collect(): List<UiEntityOfRecycler> {

        val entityForLabelButtonPair = createEntityForLabelButtonPair()
        val entityForSubtitle = createEntityForSubtitle()
        val entityForTwoColumnBoldAmount = createEntityForTwoColumnBoldAmount()

        return listOf(entityForLabelButtonPair, entityForSubtitle, entityForTwoColumnBoldAmount)
    }

    private fun createEntityForLabelButtonPair(): UiEntityOfRecycler {

        val entityForLabel = UiEntityOfSkeleton(
            paddingEntity = paddingForLabel,
            isStretchedWidth = true,
            height = R.dimen.canvascore_skeleton_body1_height,
            expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
        )

        val entityForSubMenuButton = UiEntityOfSkeleton(
            paddingEntity = paddingForRightItem,
            isStretchedWidth = false,
            width = R.dimen.canvascore_width_18,
            height = R.dimen.canvascore_height_24,
        )

        val compound = UiCompound(
            uiEntities = listOf(entityForLabel, entityForSubMenuButton),
            factoryOfPortableAdapter = AdapterFactoryOfSkeleton(),
        )
        val compounds: List<UiCompound<*>> = listOf(compound)

        val entity = UiEntityOfRecycler(
            paddingEntity = paddingOfIncludedTitleItem,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfFlexboxLayoutManager(),
        )
        compounds.associateByTo(destination = entity.compoundsById, keySelector = ::byId)

        return entity
    }

    private fun createEntityForSubtitle(): UiEntityOfRecycler {

        val entityForSubtitle = UiEntityOfSkeleton(
            paddingEntity = emptyPaddingEntity,
            isStretchedWidth = true,
            height = R.dimen.canvascore_skeleton_body1_height,
            expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
        )

        val compound = UiCompound(
            uiEntities = listOf(entityForSubtitle),
            factoryOfPortableAdapter = AdapterFactoryOfSkeleton(),
        )
        val compounds: List<UiCompound<*>> = listOf(compound)

        val entity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfRecycler,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfFlexboxLayoutManager(),
        )
        compounds.associateByTo(destination = entity.compoundsById, keySelector = ::byId)

        return entity
    }

    private fun createEntityForTwoColumnBoldAmount(): UiEntityOfRecycler {

        val entityForSubtitle2 = UiEntityOfSkeleton(
            paddingEntity = paddingForLeftItem,
            isStretchedWidth = true,
            height = R.dimen.canvascore_skeleton_body1_height,
            expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
        )

        val entityForBoldAmount = UiEntityOfSkeleton(
            paddingEntity = paddingForRightItem,
            isStretchedWidth = false,
            width = R.dimen.canvascore_slide_to_complete_thumb_width,
            height = R.dimen.canvascore_skeleton_body1_height,
        )

        val compound = UiCompound(
            uiEntities = listOf(entityForSubtitle2, entityForBoldAmount),
            factoryOfPortableAdapter = AdapterFactoryOfSkeleton(),
        )
        val compounds: List<UiCompound<*>> = listOf(compound)

        val entity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfRecycler,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfFlexboxLayoutManager(),
        )
        compounds.associateByTo(destination = entity.compoundsById, keySelector = ::byId)

        return entity
    }
}