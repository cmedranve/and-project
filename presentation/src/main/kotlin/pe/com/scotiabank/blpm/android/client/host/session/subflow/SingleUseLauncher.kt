package pe.com.scotiabank.blpm.android.client.host.session.subflow

import androidx.core.util.Supplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.util.Constant
import kotlin.time.Duration

class SingleUseLauncher(
    dispatcherProvider: DispatcherProvider,
    private var _launcher: SubFlowLauncher,
    private val countDownFlow: Flow<Duration>,
): DispatcherProvider by dispatcherProvider {

    private val emptyLauncher: SubFlowLauncher by lazy {
        SubFlowLauncher(
            shortcutId = Constant.EMPTY_STRING,
            shortcutName = Constant.EMPTY_STRING,
            analyticValue = Constant.EMPTY_STRING,
            factorySupplier = Supplier(::EmptySubFlowFactory),
        )
    }

    val launcher: SubFlowLauncher
        get() {
            val value: SubFlowLauncher = _launcher
            _launcher = emptyLauncher
            return value
        }

    suspend fun countDownLifetime() = withContext(defaultDispatcher) {
        countDownFlow
            .takeWhile(::isNotUsed)
            .onCompletion(::onCountDownFinished)
            .flowOn(defaultDispatcher)
            .collect()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun isNotUsed(durationRemaining: Duration): Boolean = emptyLauncher != _launcher

    @Suppress("UNUSED_PARAMETER")
    private suspend fun onCountDownFinished(collector: FlowCollector<Duration>, throwable: Throwable?) {
        _launcher = emptyLauncher
    }
}