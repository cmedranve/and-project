package pe.com.scotiabank.blpm.android.ui.list

import android.os.Parcelable
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableBinding
import pe.com.scotiabank.blpm.android.ui.list.animation.DisablerOfChangeAnimation
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationRendering
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.ToolbarRendering
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.SearchBarRendering
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.UiEntityOfMaterialSearch
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.NavigationBarRendering
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.RecyclerRendering
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel

object ComposerOfAppBarAndMain {

    @JvmStatic
    fun compose(
        owner: LifecycleOwner,
        binding: ActivityPortableBinding,
        viewModel: PortableViewModel,
    ) {
        DisablerOfChangeAnimation.disable(binding.rvMainItems)
        val viewPoolForMain = RecycledViewPool()
        binding.rvMainItems.setRecycledViewPool(viewPoolForMain)
        binding.rvMainItems.adapter = null

        DisablerOfChangeAnimation.disable(binding.rvResultItems)
        val viewPoolForResult = RecycledViewPool()
        binding.rvResultItems.setRecycledViewPool(viewPoolForResult)
        binding.rvResultItems.adapter = null

        observe(owner, viewPoolForMain, viewPoolForResult, binding, viewModel)

        binding.searchView.setupWithSearchBar(binding.appBarPortable.searchBar)
    }

    @JvmStatic
    private fun observe(
        owner: LifecycleOwner,
        viewPoolForMain: RecycledViewPool,
        viewPoolForResult: RecycledViewPool,
        binding: ActivityPortableBinding,
        viewModel: PortableViewModel,
    ) {
        viewModel.liveCompositeOfAppBarAndMain.observe(owner) { composite ->
            renderToolbar(composite.toolbarCompounds, binding)
            renderSearchBar(composite.searchBarCompounds, binding)
            RecyclerRendering.renderFrom(
                compoundsById = composite.mainCompoundsById,
                scrollingEventHandler = null,
                viewPool = viewPoolForMain,
                recyclerView = binding.rvMainItems,
            )
            RecyclerRendering.renderFrom(
                compoundsById = composite.resultCompoundsById,
                scrollingEventHandler = null,
                viewPool = viewPoolForResult,
                recyclerView = binding.rvResultItems,
            )
            renderRecyclerDecorations(composite.mainDecorationCompounds, binding.rvMainItems)
            restoreScrollPositionOnViewRecreated(binding.rvMainItems, viewModel)
            NavigationBarRendering.renderSingleFrom(composite.navigationCompoundsById, binding.bnv)
        }
    }

    @JvmStatic
    private fun renderToolbar(
        compounds: List<UiCompoundOfSingle<UiEntityOfToolbar>>,
        binding: ActivityPortableBinding,
    ) {
        ToolbarRendering.renderSingleFrom(
            compounds = compounds,
            appBarLayout = binding.appBarPortable.appBar,
            toolbar = binding.appBarPortable.toolbar,
        )
    }

    @JvmStatic
    private fun renderSearchBar(
        compounds: List<UiCompoundOfSingle<UiEntityOfMaterialSearch>>,
        binding: ActivityPortableBinding,
    ) {
        SearchBarRendering.renderSingleFrom(
            compounds = compounds,
            appBarLayout = binding.appBarPortable.appBar,
            searchBar = binding.appBarPortable.searchBar,
            searchView = binding.searchView,
        )
    }

    @JvmStatic
    private fun renderRecyclerDecorations(
        compounds: List<DecorationCompound>,
        rvItems: RecyclerView,
    ) {
        val compound: DecorationCompound = compounds.firstOrNull() ?: return
        val rendering: DecorationRendering = compound.rendering
        rendering.render(rvItems, compound.resId, compound.positions)
        rvItems.invalidateItemDecorations()
    }

    @JvmStatic
    private fun restoreScrollPositionOnViewRecreated(
        rvItems: RecyclerView,
        viewModel: PortableViewModel,
    ) {
        val layoutManager: LayoutManager = rvItems.layoutManager ?: return
        val recyclingState: Parcelable = viewModel.recyclingState ?: return
        layoutManager.onRestoreInstanceState(recyclingState)
    }
}
