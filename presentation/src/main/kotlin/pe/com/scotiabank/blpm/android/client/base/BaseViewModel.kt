package pe.com.scotiabank.blpm.android.client.base

import androidx.lifecycle.MutableLiveData
import java.lang.ref.WeakReference

open class BaseViewModel<N> : NewBaseViewModel() {
    //TODO() Migrate remaining activities and fragments to use loadingV2
    @Deprecated("Use the new loadingV2 method", level = DeprecationLevel.WARNING)
    val loading = MutableLiveData<Boolean>()

    //TODO() Migrate remaining activities and fragments to use errorMessageV2
    @Deprecated("Use the new errorMessageV2 method", level = DeprecationLevel.WARNING)
    val errorMessage = MutableLiveData<BaseAppearErrorMessage>()
    private var navigator: WeakReference<N>? = null

    fun getNavigator(): N? {
        return navigator?.get()
    }

    fun setNavigator(navigator: N) {
        this.navigator = WeakReference(navigator)
    }
}
