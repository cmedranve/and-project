package pe.com.scotiabank.blpm.android.client.dashboard

import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory

class DashboardFactory(systemDataFactory: SystemDataFactory) {

    companion object {
        const val SCREEN_NAME = "Inicio | Mis Cuentas"
        const val START_MENU = "inicio"
        const val MY_LIST_MENU = "mi lista"
        const val NEWS_MENU = "avisos"
        const val MY_ACCOUNT_MENU = "mi cuenta"
        private const val MENU_EVENT = "menuEvent"
        private const val BOTTOM_MENU = "bottom menu"
        private const val TOP_MENU = "top menu"
    }

    private val dataEvent: AnalyticsEvent = systemDataFactory.createGenericAnalyticsEvent()
        .buildUpon()
        .setName(MENU_EVENT)
        .add(AnalyticsBaseConstant.ORIGIN_SECTION, SCREEN_NAME)
        .add(AnalyticsBaseConstant.EVENT_ACTION, AnalyticsBaseConstant.CLICK)
        .build()

    fun createBottomMenuEvent(menuName: String): AnalyticsEvent = dataEvent
        .buildUpon()
        .add(AnalyticsBaseConstant.EVENT_CATEGORY, BOTTOM_MENU)
        .add(AnalyticsBaseConstant.EVENT_LABEL, menuName)
        .build()

    fun createTopMenuEvent(menuName: String): AnalyticsEvent = dataEvent
        .buildUpon()
        .add(AnalyticsBaseConstant.EVENT_CATEGORY, TOP_MENU)
        .add(AnalyticsBaseConstant.EVENT_LABEL, menuName)
        .build()
}