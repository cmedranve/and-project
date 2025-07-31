package pe.com.scotiabank.blpm.android.client.base

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.UiEntityOfMaterialSearch
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.footer.ComposerOfFooter
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.SwipeState
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.UiEntityOfSwipe

class CompositeRegistry(
    private val toolbarComposite: CompositeOfSingle<UiEntityOfToolbar>? = null,
    mainTopComposites: List<Composite> = emptyList(),
    mainBottomComposites: List<Composite> = emptyList(),
    mainDecorationCompounds: List<DecorationCompound> = emptyList(),
    private val anchoredBottomComposites: List<Composite> = emptyList(),
    private val searchBarComposite: CompositeOfSingle<UiEntityOfMaterialSearch>? = null,
    resultTopComposites: List<Composite> = emptyList(),
    resultBottomComposites: List<Composite> = emptyList(),
    resultDecorationCompounds: List<DecorationCompound> = emptyList(),
) {

    val isAnyAnchored: Boolean
        get() = anchoredBottomComposites.isNotEmpty()

    val swipeEntity: UiEntityOfSwipe by lazy {
        UiEntityOfSwipe(state = SwipeState.DISABLED)
    }

    private val registryOfMainRecycler: CompositeRegistryOfRecycler = CompositeRegistryOfRecycler(
        topComposites = mainTopComposites,
        bottomComposites = mainBottomComposites,
        decorationCompounds = mainDecorationCompounds,
        composerOfFooter = ComposerOfFooter(id = MAIN_FOOTER_ID),
    )

    private val registryOfResultRecycler: CompositeRegistryOfRecycler = CompositeRegistryOfRecycler(
        topComposites = resultTopComposites,
        bottomComposites = resultBottomComposites,
        decorationCompounds = resultDecorationCompounds,
        composerOfFooter = ComposerOfFooter(id = RESULT_FOOTER_ID),
    )

    suspend fun recomposeToolbarCompounds(): List<UiCompoundOfSingle<UiEntityOfToolbar>> {
        toolbarComposite?.recomposeItselfIfNeeded()
        return toolbarComposite?.compounds.orEmpty()
    }

    suspend fun recomposeMainRecyclerEntity(): UiEntityOfRecycler = registryOfMainRecycler.recomposeIfNeeded()

    suspend fun recomposeSearchBarCompounds(): List<UiCompoundOfSingle<UiEntityOfMaterialSearch>> {
        searchBarComposite?.recomposeItselfIfNeeded()
        return searchBarComposite?.compounds.orEmpty()
    }

    suspend fun recomposeResultRecyclerEntity(): UiEntityOfRecycler = registryOfResultRecycler.recomposeIfNeeded()

    suspend fun recomposeAnchoredBottomCompounds(): List<UiCompound<*>> {
        val anchoredBottomCompounds: MutableList<UiCompound<*>> = mutableListOf()
        anchoredBottomComposites.forEach { composite -> addAll(composite, anchoredBottomCompounds) }
        return anchoredBottomCompounds
    }

    private suspend fun addAll(composite: Composite, compounds: MutableList<UiCompound<*>>) {
        composite.recomposeItselfIfNeeded()
        compounds.addAll(composite.compounds)
    }

    companion object {

        private val MAIN_FOOTER_ID: Long = randomLong()
        private val RESULT_FOOTER_ID: Long = randomLong()
    }
}
