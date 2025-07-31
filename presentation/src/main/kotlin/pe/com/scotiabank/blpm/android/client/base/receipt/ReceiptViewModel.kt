package pe.com.scotiabank.blpm.android.client.base.receipt

import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.payment.creditcard.analytic.CreditCardEvent
import pe.com.scotiabank.blpm.android.client.payment.creditcard.analytic.success.CreditCardSuccessAnalyticModel
import pe.com.scotiabank.blpm.android.client.payment.creditcard.analytic.summary.CreditCardSummaryAnalyticModel
import pe.com.scotiabank.blpm.android.client.payment.loan.analytic.success.LoanSuccessAnalyticModel

class ReceiptViewModel(
    private val analyticModel: AnalyticModel,
    private val loanSuccessAnalyticModel: LoanSuccessAnalyticModel,
    private val creditCardSuccessAnalyticModel: CreditCardSuccessAnalyticModel,
    private val creditCardSummaryAnalyticModel: CreditCardSummaryAnalyticModel,
) : NewBaseViewModel() {

    fun sendScreenEvent() {
        sendAnalyticEvent(ReceiptEvent.SCREEN)
    }

    private fun sendAnalyticEvent(event: ReceiptEvent, data: Map<String, Any?> = mapOf()) {
        val eventData = AnalyticEventData(event, data)
        analyticModel.sendEvent(eventData)
    }

    fun sendClickEvent(label: String) {
        val data: Map<String, Any?> = mapOf(AnalyticsBaseConstant.EVENT_LABEL to label)
        sendAnalyticEvent(ReceiptEvent.CLICK, data)
    }

    fun sendPopupEvent(popupName: String, label: String) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.POPUP_NAME to popupName,
            AnalyticsBaseConstant.EVENT_LABEL to label,
        )
        sendAnalyticEvent(ReceiptEvent.POPUP, data)
    }

    fun sendClickPopupEvent(popupName: String, label: String) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.POPUP_NAME to popupName,
            AnalyticsBaseConstant.EVENT_LABEL to label,
        )
        sendAnalyticEvent(ReceiptEvent.CLICK_POPUP, data)
    }

    fun onLoanScreenEvent() {
        sendLoanAnalyticEvent(AnalyticEvent.SCREEN)
    }

    fun onClickLoanEvent(label: String) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.EVENT_LABEL to label,
        )
        sendLoanAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    private fun sendLoanAnalyticEvent(event: AnalyticEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        loanSuccessAnalyticModel.sendEvent(eventData)
    }

    fun onCreditCardSuccessScreenEvent() {
        sendCreditCardSuccessAnalyticEvent(AnalyticEvent.SCREEN)
    }

    fun onClickCreditCardSuccessEvent(label: String) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.EVENT_LABEL to label
        )
        sendCreditCardSuccessAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    fun onCreditCardSuccessPopUpScreenEvent(
        popUpName: String,
        screenCategory: String,
        subProcessType: String,
    ) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.POPUP_NAME to popUpName,
            AnalyticsConstant.SCREEN_CATEGORY to screenCategory,
            AnalyticsConstant.TYPE_SUBPROCESS to subProcessType,
        )
        sendCreditCardSuccessAnalyticEvent(AnalyticEvent.POP_UP, data)
    }

    fun onClickCreditCardSuccessPopUpEvent(
        label: String,
        popUpName: String,
        screenCategory: String,
    ) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.EVENT_LABEL to label,
            AnalyticsConstant.POPUP_NAME to popUpName,
            AnalyticsConstant.SCREEN_CATEGORY to screenCategory,
        )
        sendCreditCardSuccessAnalyticEvent(AnalyticEvent.POP_UP_CLICK, data)
    }

    private fun sendCreditCardSuccessAnalyticEvent(
        event: AnalyticEvent,
        data: Map<String, Any?> = emptyMap(),
    ) {
        val eventData = AnalyticEventData(event, data)
        creditCardSuccessAnalyticModel.sendEvent(eventData)
    }

    fun onCreditCardSummaryScreenEvent(
        company: String,
        questionTwo: String,
        originAccountType: String,
        originCurrency: String,
    ) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.COMPANY to company,
            AnalyticsConstant.QUESTION_TWO to questionTwo,
            AnalyticsConstant.TYPE_OF_ORIGIN_ACCOUNT to originAccountType,
            AnalyticsConstant.ORIGIN_CURRENCY to originCurrency,
        )
        sendCreditCardSummaryAnalyticEvent(AnalyticEvent.SCREEN, data)
    }

    fun onClickCreditCardSummaryEvent(label: String) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.EVENT_LABEL to label
        )
        sendCreditCardSummaryAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    fun onClickPaymentCreditCardSummaryEvent(amountPen: String, amountUsd: String, currency: String) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.AMOUNT_PEN to amountPen,
            AnalyticsConstant.AMOUNT_USD to amountUsd,
            AnalyticsConstant.CURRENCY to currency,
        )
        sendCreditCardSummaryAnalyticEvent(CreditCardEvent.PAYMENT_DETAIL_CLICK_ACTION, data)
    }

    fun onCreditCardSummaryPopUpScreenEvent(
        popUpName: String,
        screenCategory: String,
        subProcessType: String,
    ) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.POPUP_NAME to popUpName,
            AnalyticsConstant.SCREEN_CATEGORY to screenCategory,
            AnalyticsConstant.TYPE_SUBPROCESS to subProcessType,
        )
        sendCreditCardSummaryAnalyticEvent(AnalyticEvent.POP_UP, data)
    }

    fun onClickCreditCardSummaryPopUpEvent(
        label: String,
        popUpName: String,
        screenCategory: String,
    ) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.EVENT_LABEL to label,
            AnalyticsConstant.POPUP_NAME to popUpName,
            AnalyticsConstant.SCREEN_CATEGORY to screenCategory,
        )
        sendCreditCardSummaryAnalyticEvent(AnalyticEvent.POP_UP_CLICK, data)
    }

    private fun sendCreditCardSummaryAnalyticEvent(
        event: AnalyticEvent,
        data: Map<String, Any?> = emptyMap(),
    ) {
        val eventData = AnalyticEventData(event, data)
        creditCardSummaryAnalyticModel.sendEvent(eventData)
    }

    private fun sendCreditCardSummaryAnalyticEvent(
        event: CreditCardEvent,
        data: Map<String, Any?> = emptyMap(),
    ) {
        val eventData = AnalyticEventData(event, data)
        creditCardSummaryAnalyticModel.sendEvent(eventData)
    }
}
