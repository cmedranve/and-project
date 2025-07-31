package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet.AlertBannerInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet.TimerInfo

enum class AtmCardState(
    val alertBannerInfo: AlertBannerInfo,
    val timerInfo: TimerInfo,
) {
    FULL_ERROR(
        alertBannerInfo = AlertBannerInfo.FULL_ERROR,
        timerInfo = TimerInfo.HIDDEN_DATA
    ),
    ENCRYPTED(
        alertBannerInfo = AlertBannerInfo.NONE,
        timerInfo = TimerInfo.HIDDEN_DATA
    ),
    DECRYPTED(
        alertBannerInfo = AlertBannerInfo.NONE,
        timerInfo = TimerInfo.DECRYPTED_DATA
    ),
    CVV_ERROR(
        alertBannerInfo = AlertBannerInfo.CVV_ERROR,
        timerInfo = TimerInfo.DECRYPTED_DATA_WITHOUT_CVV
    ),
    LOCKED_CARD(
        alertBannerInfo = AlertBannerInfo.CARD_LOCKED,
        timerInfo = TimerInfo.HIDDEN_DATA
    ),
    LOCKED_ADDITIONAL_CARD(
        alertBannerInfo = AlertBannerInfo.ADDITIONAL_CARD_LOCKED,
        timerInfo = TimerInfo.HIDDEN_DATA
    ),
    DISABLED_PURCHASES_FOR_CARD(
        alertBannerInfo = AlertBannerInfo.CARD_PURCHASES_DISABLED,
        timerInfo = TimerInfo.HIDDEN_DATA
    ),
    DISABLED_PURCHASES_FOR_ADDITIONAL_CARD(
        alertBannerInfo = AlertBannerInfo.ADDITIONAL_CARD_PURCHASES_DISABLED,
        timerInfo = TimerInfo.HIDDEN_DATA
    ),
    UNAVAILABLE_CARD(
        alertBannerInfo = AlertBannerInfo.UNAVAILABLE_CARD,
        timerInfo = TimerInfo.HIDDEN_DATA
    ),
}
