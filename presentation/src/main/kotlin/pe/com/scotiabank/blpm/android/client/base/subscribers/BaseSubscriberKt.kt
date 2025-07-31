package pe.com.scotiabank.blpm.android.client.base.subscribers

import io.reactivex.observers.DisposableObserver

/**
 * This class is intented to be used inside a ViewModel and to be cleared in the viewmodel.onCleared()
 * function using the unsubscribe function of the use case. This way we avoid getting viewmodel leaks.
 *
 * Use this if you wish to consume a use case from a Kotlin ViewModel. It will reduce the necessary
 * boilerplate code.
 * @param <T> The Entity class that will be returned by the web service.
 */
class BaseSubscriberKt<T : Any>(
    private val success: (T) -> Unit,
    private val error: (Throwable) -> Unit
) : DisposableObserver<T>() {
    override fun onNext(t: T) {
        success(t)
    }

    override fun onError(e: Throwable) {
        error(e)
    }

    override fun onComplete() {
        //Not required
    }
}
