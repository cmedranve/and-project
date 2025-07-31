package pe.com.scotiabank.blpm.android.client.cardsettings.analytics

import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

enum class AnalyticEvent {

    VIEW,
    CLICK_ACTION,
    DIGITAL_WALLET_LOAD,
    SAVE_ACTION;

    companion object {

        @JvmStatic
        fun filterViewEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = VIEW == eventData.event

        @JvmStatic
        fun filterClickActionEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = CLICK_ACTION == eventData.event

        @JvmStatic
        fun filterDigitalWalletLoadEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = DIGITAL_WALLET_LOAD == eventData.event

        @JvmStatic
        fun filterSaveActionEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = SAVE_ACTION == eventData.event
    }
}
