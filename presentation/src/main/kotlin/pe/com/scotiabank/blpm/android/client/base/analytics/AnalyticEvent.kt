package pe.com.scotiabank.blpm.android.client.base.analytics

import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant

enum class AnalyticEvent {
    ERROR_SCREEN,
    ERROR_CLICK,
    SCREEN,
    CONTINUE,
    CLICK,
    POP_UP,
    POP_UP_CLICK,
    HST_LOADED,
    ADD_DEFAULT_PARAMETER_GROUP,
    CLEAR_DEFAULT_PARAMETER_GROUP;

    companion object {

        @JvmStatic
        fun filterErrorScreenEvent(eventData: AnalyticEventData<*>): Boolean {
            if (ERROR_SCREEN != eventData.event) return false
            return isPushOtpEvent(eventData).not()
        }

        @JvmStatic
        fun filterErrorClickEvent(eventData: AnalyticEventData<*>): Boolean {
            if (ERROR_CLICK != eventData.event) return false
            return isPushOtpEvent(eventData).not()
        }

        @JvmStatic
        fun filterScreenViewEvent(eventData: AnalyticEventData<*>): Boolean {
            if (SCREEN != eventData.event) return false
            return isPushOtpEvent(eventData).not()
        }

        @JvmStatic
        fun filterContinueEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = CONTINUE == eventData.event

        @JvmStatic
        fun filterClickEvent(eventData: AnalyticEventData<*>): Boolean {
            if (CLICK != eventData.event) return false
            return isPushOtpEvent(eventData).not()
        }

        @JvmStatic
        fun filterPopUpEvent(eventData: AnalyticEventData<*>): Boolean {
            if (POP_UP != eventData.event) return false
            return isPushOtpEvent(eventData).not()
        }

        @JvmStatic
        fun filterPopUpClickEvent(eventData: AnalyticEventData<*>): Boolean {
            if (POP_UP_CLICK != eventData.event) return false
            return isPushOtpEvent(eventData).not()
        }

        @JvmStatic
        fun filterInHstLoadedEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = HST_LOADED == eventData.event

        @JvmStatic
        fun filterInAddDefaultParameterGroup(
            eventData: AnalyticEventData<*>,
        ): Boolean = ADD_DEFAULT_PARAMETER_GROUP == eventData.event

        @JvmStatic
        fun filterInClearDefaultParameterGroup(
            eventData: AnalyticEventData<*>,
        ): Boolean = CLEAR_DEFAULT_PARAMETER_GROUP == eventData.event

        @JvmStatic
        fun filterPushOtpEvent(eventData: AnalyticEventData<*>): Boolean {
            if ((SCREEN == eventData.event || POP_UP == eventData.event).not()) return false
            return isPushOtpEvent(eventData)
        }

        @JvmStatic
        fun filterPushOtpClickEvent(eventData: AnalyticEventData<*>): Boolean {
            if ((CLICK == eventData.event || POP_UP_CLICK == eventData.event).not()) return false
            return isPushOtpEvent(eventData)
        }

        @JvmStatic
        fun filterPushOtpErrorEvent(eventData: AnalyticEventData<*>): Boolean {
            if (ERROR_SCREEN != eventData.event) return false
            return isPushOtpEvent(eventData)
        }

        @JvmStatic
        fun filterPushOtpErrorClickEvent(eventData: AnalyticEventData<*>): Boolean {
            if (ERROR_CLICK != eventData.event) return false
            return isPushOtpEvent(eventData)
        }

        @JvmStatic
        private fun isPushOtpEvent(
            event: AnalyticEventData<*>
        ): Boolean = event.data[AnalyticsConstant.AUTHENTICATION_CHANNEL] != null
    }
}

