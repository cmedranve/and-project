package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub

import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationRendering
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationUtil
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.AdapterFactoryOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.AdapterFactoryOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.AdapterFactoryOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfFlexboxLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler

class FactoryOfCardEntity(private val horizontalPaddingEntity: UiEntityOfPadding) {

    private val paddingEntityOfCard: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
        )
    }

    private val paddingEntityOfWholeContent: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            right = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
        )
    }

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    private val dividerPositions: List<Int> by lazy {
        listOf(Constant.ONE)
    }

    fun create(
        doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any>,
        textButtonEntities: List<UiEntityOfTextButton<Any>>,
    ): UiEntityOfCard<Any> {

        val doubleEndedImageCompound = UiCompound(
            uiEntities = listOf(doubleEndedImageEntity),
            factoryOfPortableAdapter = AdapterFactoryOfDoubleEndedImage(),
        )

        val flexboxRecyclerEntity: UiEntityOfRecycler = createFlexboxRecyclerEntity(
            textButtonEntities = textButtonEntities,
        )
        val flexboxRecyclerCompound = UiCompound(
            uiEntities = listOf(flexboxRecyclerEntity),
            factoryOfPortableAdapter = AdapterFactoryOfRecycler(),
        )

        val compounds: List<UiCompound<*>> = listOf(doubleEndedImageCompound, flexboxRecyclerCompound)

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfWholeContent,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
            decorationCompounds = createDecorationCompounds(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return UiEntityOfCard(
            paddingEntity = paddingEntityOfCard,
            recyclerEntity = recyclerEntity,
            useCompatPadding = false,
        )
    }

    private fun createFlexboxRecyclerEntity(
        textButtonEntities: List<UiEntityOfTextButton<Any>>,
    ): UiEntityOfRecycler {

        val textButtonCompound = UiCompound(
            uiEntities = textButtonEntities,
            factoryOfPortableAdapter = AdapterFactoryOfTextButton(),
        )

        val compounds: List<UiCompound<*>> = listOf(textButtonCompound)

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = emptyPaddingEntity,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfFlexboxLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    private fun createDecorationCompounds(): List<DecorationCompound> {
        val decorationCompound = DecorationCompound(
            positions = dividerPositions,
            rendering = DecorationRendering(DecorationUtil::addDividerAboveEachItem),
        )
        return listOf(decorationCompound)
    }
}
