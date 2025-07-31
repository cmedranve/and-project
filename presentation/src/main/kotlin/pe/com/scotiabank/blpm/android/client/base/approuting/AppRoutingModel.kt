package pe.com.scotiabank.blpm.android.client.base.approuting

import com.scotiabank.sdk.approuting.AppRouterEvent

interface AppRoutingModel {

    fun handleLink(deepLinkUri: String)

    fun attemptFindRoutingEvent(): AppRouterEvent?

    fun clearRoutingEvent()
}
