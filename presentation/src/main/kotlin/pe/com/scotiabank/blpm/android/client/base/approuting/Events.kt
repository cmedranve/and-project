package pe.com.scotiabank.blpm.android.client.base.approuting

import android.net.Uri
import com.scotiabank.sdk.approuting.AppRouterEvent

sealed class SealedRouterEvent(val uri: Uri): AppRouterEvent()

class BrowserEvent(uri: Uri, val url: String): SealedRouterEvent(uri)

class WebViewEvent(uri: Uri, val url: String): SealedRouterEvent(uri)

class NavigationEvent(uri: Uri, val destination: Destination) : SealedRouterEvent(uri) {

    enum class Destination {
        PRODUCTS_EDIT,
        PRODUCTS,
        PURCHASE_ERASER,
        TRANSFER_CATEGORY,
        TRANSFER_BETWEEN_MY_ACCOUNTS,
        SERVICES_INSTITUTION,
        WALLET,
        EXCHANGE_MONEY,
        CARDLESS_WITHDRAWAL,
        GOALS,
        MY_LIST,
        PLIN,
        MY_ACCOUNT,
        ADVISOR,
        CARD_CONFIGURATION,
        LIMITS,
        NOTIFICATIONS,
        BIOMETRIC,
        NOTICES,
        INSTALLMENT,
        HUB_SCOTIA_POINTS,
    }

}
