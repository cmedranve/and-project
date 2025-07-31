package pe.com.scotiabank.blpm.android.client.base.approuting

import android.net.Uri
import com.scotiabank.sdk.approuting.AppRouterEvent
import com.scotiabank.sdk.approuting.Filter
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.lang.StringBuilder

class InnerFilter : Filter {

    override fun isValidRequest(request: String?): Boolean {
        if (request.isNullOrBlank()) return false

        val uri: Uri = Uri.parse(request)
        return UriSegment.SCHEME_NAME == uri.scheme
    }

    override fun parse(request: String?): AppRouterEvent? {
        if (request.isNullOrBlank()) return null

        val uri: Uri = Uri.parse(request)
        val pathOfUri: String = uri.path ?: return null

        return EventFactory.attemptCreate(uri, pathOfUri)
    }
}

class HttpsFilter(private val innerFilter: Filter) : Filter {

    override fun isValidRequest(request: String?): Boolean {
        if (request.isNullOrBlank()) return false

        val httpsUri: Uri = Uri.parse(request)
        val embeddedUrl: String = httpsUri.getQueryParameter(UriSegment.QUERY_PARAM_EMBEDDED_URL)
            ?: return false

        return innerFilter.isValidRequest(embeddedUrl)
    }

    override fun parse(request: String?): AppRouterEvent? {
        val httpsUri: Uri = Uri.parse(request)
        val embeddedUrl: String = httpsUri.getQueryParameter(UriSegment.QUERY_PARAM_EMBEDDED_URL)
            ?: return null
        return innerFilter.parse(embeddedUrl)
    }
}

private object EventFactory {

    fun attemptCreate(uri: Uri, pathOfUri: String): AppRouterEvent? = when {
        pathOfUri.startsWith(WebViewPath.WEB_VIEW) -> createWebViewEvent(uri)
        pathOfUri.startsWith(BrowserPath.BROWSER) -> createBrowserEvent(uri)
        else -> createNavigationEvent(uri, pathOfUri)
    }

    private fun createWebViewEvent(uri: Uri): WebViewEvent? {
        val isNotFound: Boolean = uri.getQueryParameter(WebViewPath.QUERY_PARAM_VALUE)
            .isNullOrBlank()
        val urlToOpenWebView: String = uri.toString()
        return if (isNotFound) null else WebViewEvent(uri, urlToOpenWebView)
    }

    private fun createBrowserEvent(uri: Uri): BrowserEvent? {
        val urlToOpenBrowser: String = getUrlFromUri(uri) ?: return null
        return BrowserEvent(uri, urlToOpenBrowser)
    }

    private fun getUrlFromUri(uri: Uri): String? {
        val queryNames: MutableSet<String> = uri.queryParameterNames.toMutableSet()
        if (queryNames.isEmpty()) { return null }

        val queryParameterUrl: String = uri.getQueryParameter(BrowserPath.QUERY_PARAM_VALUE) ?: return null
        val url = StringBuilder(queryParameterUrl)

        val existsQueryParams: Boolean = queryNames.size > 1
        if (existsQueryParams) {
            queryNames.remove(BrowserPath.QUERY_PARAM_VALUE)
            queryNames.forEach { parameter ->
                val parameterValue: String = uri.getQueryParameter(parameter) ?: Constant.EMPTY_STRING
                url.append(getQueryParameterFormat(parameter, parameterValue))
            }
            return url.toString()
        }

        val uriFromNewUrl: Uri = Uri.parse(url.toString())
        if (uriFromNewUrl.encodedQuery == null) url.append(BrowserPath.QUERY_SEPARATOR)

        url.append(getQueryParameterFormat(BrowserPath.QUERY_UTM_MEDIUM, BrowserPath.DEFAULT_UTM_MEDIUM))
            .append(getQueryParameterFormat(BrowserPath.QUERY_UTM_SOURCE, BrowserPath.DEFAULT_UTM_SOURCE))
            .append(getQueryParameterFormat(BrowserPath.QUERY_UTM_CAMPAIGN, BrowserPath.DEFAULT_UTM_CAMPAIGN))

        return url.toString()
    }

    private fun getQueryParameterFormat(parameter: String, parameterValue: String): String {
        return BrowserPath.QUERY_AMPERSAND + parameter + BrowserPath.QUERY_ASSIGNER + parameterValue
    }

    private fun createNavigationEvent(uri: Uri, pathOfUri: String): NavigationEvent? {
        val destination: NavigationEvent.Destination = toDestination(pathOfUri)
            ?: return null
        return NavigationEvent(uri, destination)
    }

    private fun toDestination(pathOfUri: String): NavigationEvent.Destination? = when {
        pathOfUri.startsWith(NavigationPath.PRODUCTS_EDIT) -> NavigationEvent.Destination.PRODUCTS_EDIT
        pathOfUri.startsWith(NavigationPath.PRODUCTS) -> NavigationEvent.Destination.PRODUCTS
        pathOfUri.startsWith(NavigationPath.PURCHASE_ERASER) -> NavigationEvent.Destination.PURCHASE_ERASER
        pathOfUri.startsWith(NavigationPath.TRANSFER_CATEGORY) -> NavigationEvent.Destination.TRANSFER_CATEGORY
        pathOfUri.startsWith(NavigationPath.TRANSFER_BETWEEN_MY_ACCOUNTS) -> NavigationEvent.Destination.TRANSFER_BETWEEN_MY_ACCOUNTS
        pathOfUri.startsWith(NavigationPath.SERVICES_INSTITUTION) -> NavigationEvent.Destination.SERVICES_INSTITUTION
        pathOfUri.startsWith(NavigationPath.WALLET) -> NavigationEvent.Destination.WALLET
        pathOfUri.startsWith(NavigationPath.EXCHANGE_MONEY) -> NavigationEvent.Destination.EXCHANGE_MONEY
        pathOfUri.startsWith(NavigationPath.CARDLESS_WITHDRAWAL) -> NavigationEvent.Destination.CARDLESS_WITHDRAWAL
        pathOfUri.startsWith(NavigationPath.GOALS) -> NavigationEvent.Destination.GOALS
        pathOfUri.startsWith(NavigationPath.MY_LIST) -> NavigationEvent.Destination.MY_LIST
        pathOfUri.startsWith(NavigationPath.PLIN) -> NavigationEvent.Destination.PLIN
        pathOfUri.startsWith(NavigationPath.MY_ACCOUNT) -> NavigationEvent.Destination.MY_ACCOUNT
        pathOfUri.startsWith(NavigationPath.ADVISOR) -> NavigationEvent.Destination.ADVISOR
        pathOfUri.startsWith(NavigationPath.CARD_CONFIGURATION) -> NavigationEvent.Destination.CARD_CONFIGURATION
        pathOfUri.startsWith(NavigationPath.LIMITS) -> NavigationEvent.Destination.LIMITS
        pathOfUri.startsWith(NavigationPath.NOTIFICATIONS) -> NavigationEvent.Destination.NOTIFICATIONS
        pathOfUri.startsWith(NavigationPath.BIOMETRIC) -> NavigationEvent.Destination.BIOMETRIC
        pathOfUri.startsWith(NavigationPath.NOTICES) -> NavigationEvent.Destination.NOTICES
        pathOfUri.startsWith(NavigationPath.INSTALLMENT) -> NavigationEvent.Destination.INSTALLMENT
        pathOfUri.startsWith(NavigationPath.HUB_SCOTIA_POINTS) -> NavigationEvent.Destination.HUB_SCOTIA_POINTS
        else -> null
    }
}

private object UriSegment {

    const val QUERY_PARAM_EMBEDDED_URL = "embeddedURL"
    const val SCHEME_NAME = "scotiabankpe"
    const val SCHEME = "$SCHEME_NAME://"
}

private object NavigationPath {

    const val PRODUCTS_EDIT = "/productsEdit"
    const val PRODUCTS = "/products"
    const val PURCHASE_ERASER = "/purchaseEraser"
    const val TRANSFER_CATEGORY = "/transferCategory"
    const val TRANSFER_BETWEEN_MY_ACCOUNTS = "/transfersOwn"
    const val SERVICES_INSTITUTION = "/servicesOrInstitutions"
    const val WALLET = "/wallet"
    const val EXCHANGE_MONEY = "/exchange"
    const val CARDLESS_WITHDRAWAL = "/withdrawal"
    const val GOALS = "/pfm"
    const val MY_LIST = "/myList"
    const val PLIN = "/contacts"
    const val MY_ACCOUNT = "/userMenu"
    const val ADVISOR = "/yourAdvisor"
    const val CARD_CONFIGURATION = "/cardConfiguration"
    const val LIMITS = "/limits"
    const val NOTIFICATIONS = "/pushNotificationsSettings"
    const val BIOMETRIC = "/biometrics"
    const val NOTICES = "/notices"
    const val INSTALLMENT = "/installment"
    const val HUB_SCOTIA_POINTS = "/scotiaPointsHub"
}

private object WebViewPath {

    const val WEB_VIEW = "/webView"
    const val QUERY_PARAM_VALUE = "path"
}

private object BrowserPath {

    const val BROWSER = "/browser"
    const val QUERY_PARAM_VALUE = "url"
    const val QUERY_UTM_MEDIUM = "utm_medium"
    const val QUERY_UTM_SOURCE = "utm_source"
    const val QUERY_UTM_CAMPAIGN = "utm_campaign"
    const val DEFAULT_UTM_MEDIUM = "push"
    const val DEFAULT_UTM_SOURCE = "push"
    const val DEFAULT_UTM_CAMPAIGN = "sin-campana"
    const val QUERY_ASSIGNER = "="
    const val QUERY_AMPERSAND = "&"
    const val QUERY_SEPARATOR = "?"
}

object NavigationPathFactory {

    private const val QUERY_KEY = "?"
    private const val QUERY_ASSIGNER = "="
    const val PRODUCT_ID = "productId"

    @JvmStatic
    fun createProductPathBy(productId: String): String {
        return "${UriSegment.SCHEME}${NavigationPath.PRODUCTS}$QUERY_KEY$PRODUCT_ID$QUERY_ASSIGNER$productId"
    }

    @JvmStatic
    fun createProductsEditPath(): String {
        return "${UriSegment.SCHEME}${NavigationPath.PRODUCTS_EDIT}"
    }

    @JvmStatic
    fun createTransferPath(): String {
        return "${UriSegment.SCHEME}${NavigationPath.TRANSFER_CATEGORY}"
    }

}
