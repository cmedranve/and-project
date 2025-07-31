package pe.com.scotiabank.blpm.android.client.base.coroutine

import kotlinx.coroutines.*

class ProviderAgentForCoroutine(
    dispatcherHolder: DispatcherProvider,
    completableJob: CompletableJob = SupervisorJob(),
): ProviderForCoroutine, DispatcherProvider by dispatcherHolder {

    // No need to cancel this scope as it'll be torn down with the process
    override val appScope: CoroutineScope = CoroutineScope(
        context = completableJob.plus(mainDispatcher.immediate)
    )
}
