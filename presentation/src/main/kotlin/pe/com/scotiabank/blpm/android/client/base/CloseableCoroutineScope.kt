package pe.com.scotiabank.blpm.android.client.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * [CoroutineScope] that provides a method to [close] it, causing the rejection of any new tasks and
 * cleanup of all underlying resources associated with the scope.
 */
internal class CloseableCoroutineScope(
    override val coroutineContext: CoroutineContext,
) : AutoCloseable, CoroutineScope {

    constructor(coroutineScope: CoroutineScope) : this(coroutineScope.coroutineContext)

    override fun close() = coroutineContext.cancel()
}
