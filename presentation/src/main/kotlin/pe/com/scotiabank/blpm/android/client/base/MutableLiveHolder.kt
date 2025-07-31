package pe.com.scotiabank.blpm.android.client.base

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pe.com.scotiabank.blpm.android.client.base.launcheditor.EnablerOfScreenshotControl
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfAppBarAndMain
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.MutableCompositeOfAppBarAndMain
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.UiEntityOfMaterialSearch
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.UiEntityOfNavigation
import pe.com.scotiabank.blpm.android.ui.list.items.page.UiEntityOfPage
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.UiEntityOfSwipe

class MutableLiveHolder(
    private val childRegistry: ChildRegistry,
    private val environmentHolder: EnvironmentHolder,
    private val mutableLiveComposite: MutableLiveData<CompositeOfAppBarAndMain> = MutableLiveData(),
    val anchoredBottom: MutableLiveData<List<UiCompound<*>>> = MutableLiveData(),
    private val mainLoading: MutableLiveData<Int> = MutableLiveData(),
    private val resultLoading: MutableLiveData<Int> = MutableLiveData(),
    private val windowSecureFlag: MutableStateFlow<Boolean> = MutableStateFlow(false),
    private val medalliaInterceptFlag: MutableStateFlow<Boolean> = MutableStateFlow(false),
    private val navigationCompoundsById: LinkedHashMap<Long, UiCompoundOfSingle<UiEntityOfNavigation>> = LinkedHashMap(),
) : LiveHolder {

    override val liveCompositeOfAppBarAndMain: LiveData<CompositeOfAppBarAndMain>
        get() = mutableLiveComposite
    override val liveAnchoredBottomCompounds: LiveData<List<UiCompound<*>>>
        get() = anchoredBottom

    override val liveMainLoading: LiveData<Int>
        get() = mainLoading

    override val liveResultLoading: LiveData<Int>
        get() = resultLoading

    private val isScreenshotEnabled: Boolean
        get() {
            val enabler: EnablerOfScreenshotControl = environmentHolder as? EnablerOfScreenshotControl
                ?: return false
            return enabler.isScreenshotEnabled
        }

    override val windowSecureFlagFlow: StateFlow<Boolean>
        get() = windowSecureFlag

    override val medalliaInterceptFlagFlow: StateFlow<Boolean>
        get() = medalliaInterceptFlag

    fun notifyAppBarAndMain(
        coordinatorId: Long,
        toolbarCompounds: List<UiCompoundOfSingle<UiEntityOfToolbar>>,
        swipeEntity: UiEntityOfSwipe,
        mainRecyclerEntity: UiEntityOfRecycler,
        searchBarCompounds: List<UiCompoundOfSingle<UiEntityOfMaterialSearch>>,
        resultRecyclerEntity: UiEntityOfRecycler,
        isMedalliaInterceptEnabled: Boolean,
    ) {
        if (coordinatorId != childRegistry.currentDeepChild.id) return

        notifyWindowSecureFlag()
        doNotifyAppBarAndMain(
            coordinatorId = coordinatorId,
            toolbarCompounds = toolbarCompounds,
            swipeEntity = swipeEntity,
            mainRecyclerEntity = mainRecyclerEntity,
            searchBarCompounds = searchBarCompounds,
            resultRecyclerEntity = resultRecyclerEntity,
        )
        notifyMedalliaInterceptFlag(isMedalliaInterceptEnabled)
    }

    private fun notifyWindowSecureFlag() {
        if (windowSecureFlag.value == isScreenshotEnabled) return

        windowSecureFlag.value = isScreenshotEnabled
    }

    private fun notifyMedalliaInterceptFlag(isInterceptEnabled: Boolean) {
        if (medalliaInterceptFlag.value == isInterceptEnabled) return

        medalliaInterceptFlag.value = isInterceptEnabled
    }

    private fun doNotifyAppBarAndMain(
        coordinatorId: Long,
        toolbarCompounds: List<UiCompoundOfSingle<UiEntityOfToolbar>>,
        swipeEntity: UiEntityOfSwipe,
        mainRecyclerEntity: UiEntityOfRecycler,
        searchBarCompounds: List<UiCompoundOfSingle<UiEntityOfMaterialSearch>>,
        resultRecyclerEntity: UiEntityOfRecycler,
    ) {
        val pageEntity = UiEntityOfPage(
            swipeEntity = swipeEntity,
            recyclerEntity = mainRecyclerEntity,
            id = coordinatorId,
        )
        val mutableComposite = MutableCompositeOfAppBarAndMain(
            toolbarCompounds = toolbarCompounds.toMutableList(),
            pageEntities = mutableListOf(pageEntity),
            navigationCompoundsById = navigationCompoundsById,
            searchBarCompounds = searchBarCompounds.toMutableList(),
            resultCompoundsById = resultRecyclerEntity.compoundsById,
        )

        mutableLiveComposite.value = mutableComposite
    }

    fun notifyMainLoadingVisibility(isVisible: Boolean) {
        val visibility: Int = if (isVisible) View.VISIBLE else View.GONE
        mainLoading.postValue(visibility)
    }

    fun notifyResultLoadingVisibility(isVisible: Boolean) {
        val visibility: Int = if (isVisible) View.VISIBLE else View.GONE
        resultLoading.postValue(visibility)
    }

    fun addNavigationCompound(compound: UiCompoundOfSingle<UiEntityOfNavigation>) {
        navigationCompoundsById[compound.id] = compound
    }

    fun removeNavigationCompound(id: Long) {
        navigationCompoundsById.remove(id)
    }
}
