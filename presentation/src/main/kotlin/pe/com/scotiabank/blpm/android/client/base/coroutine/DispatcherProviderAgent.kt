package pe.com.scotiabank.blpm.android.client.base.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

class DispatcherProviderAgent: DispatcherProvider {

    override val mainDispatcher: MainCoroutineDispatcher
        get() = Dispatchers.Main

    override val defaultDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
}
