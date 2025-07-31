package pe.com.scotiabank.blpm.android.client.base

import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.items.footer.ComposerOfFooter
import pe.com.scotiabank.blpm.android.ui.list.items.footer.UiEntityOfFooter
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfRootLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler

class CompositeRegistryOfRecycler(
    private val topComposites: List<Composite>,
    private val bottomComposites: List<Composite>,
    private val decorationCompounds: List<DecorationCompound>,
    private val composerOfFooter: ComposerOfFooter,
) {

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    private val recyclerEntity: UiEntityOfRecycler by lazy {
        UiEntityOfRecycler(
            paddingEntity = emptyPaddingEntity,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfRootLayoutManager(),
            decorationCompounds = decorationCompounds,
        )
    }

    suspend fun recomposeIfNeeded(): UiEntityOfRecycler {
        val compoundsById: LinkedHashMap<Long, UiCompound<*>> = LinkedHashMap()
        topComposites.forEach { composite -> associateById(composite, compoundsById) }

        val bottomCompoundsById: LinkedHashMap<Long, UiCompound<*>> = LinkedHashMap()
        bottomComposites.forEach { composite -> associateById(composite, bottomCompoundsById) }
        addFooter(compoundsById, bottomCompoundsById)
        return recyclerEntity.copyWith(compoundsById)
    }

    private suspend fun associateById(
        composite: Composite,
        compoundsById: LinkedHashMap<Long, UiCompound<*>>,
    ) {
        composite.recomposeItselfIfNeeded()
        composite.compounds.associateByTo(destination = compoundsById, keySelector = ::byId)
    }

    private fun addFooter(
        compoundsById: LinkedHashMap<Long, UiCompound<*>>,
        bottomCompoundsById: LinkedHashMap<Long, UiCompound<*>>,
    ) {
        val footerCompound: UiCompound<UiEntityOfFooter> = composerOfFooter.composeUiData(
            compoundsById = bottomCompoundsById,
        )
        compoundsById[footerCompound.id] = footerCompound
    }
}
