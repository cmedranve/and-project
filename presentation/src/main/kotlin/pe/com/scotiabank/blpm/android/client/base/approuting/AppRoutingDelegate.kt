package pe.com.scotiabank.blpm.android.client.base.approuting

import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.sdk.approuting.AppRouterEvent
import com.scotiabank.sdk.approuting.AppRoutingManager
import com.scotiabank.sdk.approuting.RouterExecutor
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.AppRoutingFactory
import java.util.concurrent.ConcurrentHashMap

class AppRoutingDelegate(
    private val analyticsModel: AppRoutingAnalyticsModel,
    private val receiverOfAppRoutingEvents: InstanceReceiver,
): AppRoutingModel, RouterExecutor {

    private val routingEventsByKey: MutableMap<Int, AppRouterEvent?> by lazy {
        ConcurrentHashMap()
    }

    private val filterManager: AppRoutingManager by lazy {
        createAppRoutingManager()
    }

    private fun createAppRoutingManager(): AppRoutingManager {
        val appRoutingManager = AppRoutingManager(this)

        val innerFilter = InnerFilter()
        val httpsFilter = HttpsFilter(innerFilter)
        appRoutingManager.addFilter(httpsFilter)
        appRoutingManager.addFilter(innerFilter)

        return appRoutingManager
    }

    override fun handleLink(deepLinkUri: String) {
        filterManager.handleLink(deepLinkUri)
    }

    override fun execute(event: AppRouterEvent?) {
        val sealedRouterEvent: SealedRouterEvent = event as? SealedRouterEvent ?: return
        analyticsModel.sendEvent(sealedRouterEvent.uri)

        if (sealedRouterEvent is BrowserEvent) {
            receiverOfAppRoutingEvents.receive(sealedRouterEvent)
            return
        }

        routingEventsByKey[SINGLE_KEY] = event
        receiverOfAppRoutingEvents.receive(sealedRouterEvent)
    }

    override fun attemptFindRoutingEvent(): AppRouterEvent? = routingEventsByKey[SINGLE_KEY]

    override fun clearRoutingEvent() = routingEventsByKey.clear()

    companion object {

        private const val SINGLE_KEY: Int = 0
    }

    class Builder(
        private val analyticsDataGateway: AnalyticsDataGateway,
        private val analyticsFactory: AppRoutingFactory
    ) {

        fun build(
            receiverOfAppRoutingEvents: InstanceReceiver,
        ): AppRoutingDelegate = AppRoutingDelegate(
            analyticsModel = AppRoutingAnalyticsModel(analyticsDataGateway, analyticsFactory),
            receiverOfAppRoutingEvents = receiverOfAppRoutingEvents
        )
    }
}
