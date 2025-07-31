package pe.com.scotiabank.blpm.android.client.base.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher

interface DispatcherProvider {

    val mainDispatcher: MainCoroutineDispatcher
    val defaultDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
}
