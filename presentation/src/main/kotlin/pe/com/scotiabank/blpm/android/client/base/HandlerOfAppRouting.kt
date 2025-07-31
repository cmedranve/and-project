package pe.com.scotiabank.blpm.android.client.base

import com.scotiabank.sdk.approuting.AppRouterEvent
import pe.com.scotiabank.blpm.android.client.app.AppModel

interface HandlerOfAppRouting {

    fun attemptHandleRoutingEvent(appModel: AppModel) {
        val event: AppRouterEvent = appModel.attemptFindRoutingEvent() ?: return
        handleRoutingEvent(event)
    }

    fun handleRoutingEvent(event: AppRouterEvent) {
        // do nothing unless is overridden by explicit handlers
    }
}
