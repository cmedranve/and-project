package pe.com.scotiabank.blpm.android.client.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.com.scotiabank.blpm.android.client.base.subscribers.BaseSubscriber
import pe.com.scotiabank.blpm.android.client.base.subscribers.BaseSubscriberInterface
import pe.com.scotiabank.blpm.android.client.base.subscribers.BaseSubscriberKt
import pe.com.scotiabank.blpm.android.client.util.EventWrapper

open class NewBaseViewModel : ViewModel() {

    private val loadingV2 = MutableLiveData<EventWrapper<Boolean>>()
    private val errorMessageV2 = MutableLiveData<EventWrapper<BaseAppearErrorMessage>>()

    open fun getLoadingV2(): LiveData<EventWrapper<Boolean>> {
        return loadingV2
    }

    fun setLoadingV2(loadingV2: Boolean) = with(this.loadingV2) {
        if (value == null || value!!.hasBeenHandled()) {
            value = EventWrapper(loadingV2)
        }
    }

    open fun getErrorMessageV2(): LiveData<EventWrapper<BaseAppearErrorMessage>> {
        return errorMessageV2
    }

    open fun showErrorMessage(throwable: Throwable?) {
        BaseAppearErrorMessage().apply {
            this.throwable = throwable
        }.also(this::setErrorMessageV2)
        setLoadingV2(false)
    }

    private fun setErrorMessageV2(errorMessageV2: BaseAppearErrorMessage) = with(this.errorMessageV2) {
        if (value == null || value!!.hasBeenHandled()) {
            value = EventWrapper(errorMessageV2)
        }
    }

    protected fun <T> getBaseSubscriber(onSuccess: BaseSubscriberInterface.Success<T>): BaseSubscriber<T> {
        return BaseSubscriber(onSuccess, BaseSubscriberInterface.Error(this::showErrorMessage))
    }

    protected fun <T : Any> getBaseSubscriberKt(success: (T) -> Unit): BaseSubscriberKt<T> {
        return BaseSubscriberKt(success, this::showErrorMessage)
    }
}
