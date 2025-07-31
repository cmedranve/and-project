package pe.com.scotiabank.blpm.android.client.base.searchbar

import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.ComposerOfSearchBar
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.MaterialSearchController
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.UiEntityOfMaterialSearch
import pe.com.scotiabank.blpm.android.ui.util.Constant
import java.util.concurrent.ConcurrentHashMap

class SearchBarComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfSearchBar: ComposerOfSearchBar,
    private val visibilitySupplier: Supplier<Boolean>,
) : CompositeOfSingle<UiEntityOfMaterialSearch>,
    DispatcherProvider by dispatcherProvider,
    MaterialSearchController by composerOfSearchBar
{

    private val compoundsByKey: MutableMap<Int, List<UiCompoundOfSingle<UiEntityOfMaterialSearch>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompoundOfSingle<UiEntityOfMaterialSearch>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    fun setSearch(
        hintTextForSearchBar: CharSequence,
        hintTextForSearchView: CharSequence = Constant.EMPTY,
        @StyleRes textAppearanceRes: Int = ResourcesCompat.ID_NULL,
    ) : SearchBarComposite = apply {

        composerOfSearchBar.editSearch(hintTextForSearchBar, hintTextForSearchView, textAppearanceRes)
    }

    fun setSearchView(
        isEnabled: Boolean = false,
        hintText: CharSequence,
        @StyleRes textAppearanceRes: Int = ResourcesCompat.ID_NULL,
    ) : SearchBarComposite = apply {

        composerOfSearchBar.editSearchView(isEnabled, hintText, textAppearanceRes)
    }

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompoundOfSingle<UiEntityOfMaterialSearch>> {
        val searchBarCompound = composerOfSearchBar.composeUiData(
            visibilitySupplier = visibilitySupplier,
        )
        return listOf(searchBarCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
    ) {

        fun create(
            receiver: InstanceReceiver,
            visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
        ) : SearchBarComposite = SearchBarComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfSearchBar = ComposerOfSearchBar(receiver),
            visibilitySupplier = visibilitySupplier,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
