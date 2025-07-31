package pe.com.scotiabank.blpm.android.client.host.shared

import android.os.Handler
import android.os.Looper
import com.akamai.botman.CYFMonitor
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.handler.HandlerExecutor
import pe.com.scotiabank.blpm.android.data.net.interceptor.HolderOfSensorData
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.function.Function
import java.util.function.Supplier
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MutableHolderOfSensorData : HolderOfSensorData {

    private val sensorDataById: MutableMap<Long, String> = ConcurrentHashMap()
    private val clearing: Runnable by lazy {
        Runnable(sensorDataById::clear)
    }
    private val millisecondsToElapseForClearing: Long by lazy {
        val duration: Duration = SECONDS_TO_ELAPSE_FOR_CLEARING.seconds
        duration.inWholeMilliseconds
    }

    private val mainHandler: Handler = Handler(
        Looper.getMainLooper(),
    )
    private val mainHandlerExecutor: Executor by lazy {
        HandlerExecutor(mainHandler)
    }

    private val sensorDataSupplier: Supplier<String> by lazy {
        Supplier(CYFMonitor::getSensorData)
    }
    private val emptyStringIfError: Function<Throwable, String> by lazy {
        Function(::toEmptyString)
    }

    override val sensorData: String
        get() = sensorDataById[SINGLE_ID] ?: fetchSensorData()

    private fun fetchSensorData(): String {
        sensorDataById[SINGLE_ID] = supplyNewSensorData()
        mainHandler.postDelayed(clearing, millisecondsToElapseForClearing)
        return sensorData
    }

    private fun supplyNewSensorData(): String = CompletableFuture
        .supplyAsync(sensorDataSupplier, mainHandlerExecutor)
        .exceptionally(emptyStringIfError)
        .get()

    @Suppress("UNUSED_PARAMETER")
    private fun toEmptyString(throwable: Throwable): String = Constant.EMPTY_STRING

    companion object {

        private val SINGLE_ID: Long = randomLong()
        private val SECONDS_TO_ELAPSE_FOR_CLEARING: Int
            get() = 8
    }
}
