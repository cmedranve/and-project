package pe.com.scotiabank.blpm.android.ui.list.composite

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.StateFlow
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

interface LiveHolder {

    val liveCompositeOfAppBarAndMain: LiveData<CompositeOfAppBarAndMain>
    val liveAnchoredBottomCompounds: LiveData<List<UiCompound<*>>>
    val liveMainLoading: LiveData<Int>
    val liveResultLoading: LiveData<Int>
    val windowSecureFlagFlow: StateFlow<Boolean>
    val medalliaInterceptFlagFlow: StateFlow<Boolean>
}
