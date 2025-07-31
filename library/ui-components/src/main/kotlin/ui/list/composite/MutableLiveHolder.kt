package pe.com.scotiabank.blpm.android.ui.list.composite

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.UiEntityOfMaterialSearch
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.footer.ComposerOfFooter
import pe.com.scotiabank.blpm.android.ui.list.items.footer.UiEntityOfFooter

class MutableLiveHolder(
    private val mutableLiveComposite: MutableLiveData<CompositeOfAppBarAndMain> = MutableLiveData(),
    val anchoredBottom: MutableLiveData<List<UiCompound<*>>> = MutableLiveData(),
    private val mainLoading: MutableLiveData<Int> = MutableLiveData(),
    private val resultLoading: MutableLiveData<Int> = MutableLiveData(),
    val windowSecureFlag: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val medalliaInterceptFlag: MutableStateFlow<Boolean> = MutableStateFlow(false),
    private val composerOfMainFooter: ComposerOfFooter = ComposerOfFooter(id = MAIN_FOOTER_ID),
    private val composerOfResultFooter: ComposerOfFooter = ComposerOfFooter(id = RESULT_FOOTER_ID),
) : LiveHolder {

    private val mutableComposite: MutableCompositeOfAppBarAndMain by lazy {
        MutableCompositeOfAppBarAndMain()
    }

    override val liveCompositeOfAppBarAndMain: LiveData<CompositeOfAppBarAndMain>
        get() = mutableLiveComposite
    override val liveAnchoredBottomCompounds: LiveData<List<UiCompound<*>>>
        get() = anchoredBottom

    override val liveMainLoading: LiveData<Int>
        get() = mainLoading

    override val liveResultLoading: LiveData<Int>
        get() = resultLoading

    override val windowSecureFlagFlow: StateFlow<Boolean>
        get() = windowSecureFlag

    override val medalliaInterceptFlagFlow: StateFlow<Boolean>
        get() = medalliaInterceptFlag

    fun notifyAppBarAndMain(
        appBar: List<UiCompoundOfSingle<UiEntityOfToolbar>>,
        mainTop: List<UiCompound<*>>,
        mainBottom: List<UiCompound<*>> = emptyList(),
        mainDecorationCompounds: List<DecorationCompound> = emptyList(),
        searchBar: List<UiCompoundOfSingle<UiEntityOfMaterialSearch>> = emptyList(),
        resultTop: List<UiCompound<*>> = emptyList(),
        resultBottom: List<UiCompound<*>> = emptyList(),
    ) {
        mutableComposite.toolbarCompounds.clear()
        mutableComposite.toolbarCompounds.addAll(appBar)

        mutableComposite.mainCompoundsById.clear()
        mainTop.associateByTo(destination = mutableComposite.mainCompoundsById, keySelector = ::byId)

        val mainBottomCompoundsById: LinkedHashMap<Long, UiCompound<*>> = LinkedHashMap()
        mainBottom.associateByTo(destination = mainBottomCompoundsById, keySelector = ::byId)
        val mainFooterCompound: UiCompound<UiEntityOfFooter> = composerOfMainFooter.composeUiData(
            compoundsById = mainBottomCompoundsById,
        )
        mutableComposite.mainCompoundsById[mainFooterCompound.id] = mainFooterCompound

        mutableComposite.mainDecorationCompounds.clear()
        mutableComposite.mainDecorationCompounds.addAll(mainDecorationCompounds)

        mutableComposite.searchBarCompounds.clear()
        mutableComposite.searchBarCompounds.addAll(searchBar)

        mutableComposite.resultCompoundsById.clear()
        resultTop.associateByTo(destination = mutableComposite.resultCompoundsById, keySelector = ::byId)

        val resultBottomCompoundsById: LinkedHashMap<Long, UiCompound<*>> = LinkedHashMap()
        resultBottom.associateByTo(destination = resultBottomCompoundsById, keySelector = ::byId)
        val resultFooterCompound: UiCompound<UiEntityOfFooter> = composerOfResultFooter.composeUiData(
            compoundsById = resultBottomCompoundsById,
        )
        mutableComposite.resultCompoundsById[resultFooterCompound.id] = resultFooterCompound

        mutableLiveComposite.postValue(mutableComposite)
    }

    fun notifyMainLoadingVisibility(isVisible: Boolean) {
        val visibility: Int = if (isVisible) View.VISIBLE else View.GONE
        mainLoading.postValue(visibility)
    }

    fun notifyResultLoadingVisibility(isVisible: Boolean) {
        val visibility: Int = if (isVisible) View.VISIBLE else View.GONE
        resultLoading.postValue(visibility)
    }

    companion object {

        private val MAIN_FOOTER_ID: Long = randomLong()
        private val RESULT_FOOTER_ID: Long = randomLong()
    }
}
