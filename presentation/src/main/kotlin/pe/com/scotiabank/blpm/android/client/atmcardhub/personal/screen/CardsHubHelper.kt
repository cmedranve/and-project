package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.util.Constant

class CardsHubHelper {

    fun validateTypeOfScreenCards(mainTopComposite: MainTopComposite): String {
        val numDebitCards: Int = mainTopComposite.numDebitCards
        val numCreditCards: Int = mainTopComposite.numCreditCards
        val debitCardState: UiState = mainTopComposite.compositeForDebitCard.currentState
        val creditCardState: UiState = mainTopComposite.compositeForCreditCard.currentState

        return when {
            numDebitCards > Constant.ZERO && numCreditCards > Constant.ZERO -> AnalyticsConstant.DEBIT_CREDIT
            numDebitCards > Constant.ZERO && numCreditCards == Constant.ZERO ->
                validateIfOnlyDebitCardsExists(creditCardState)
            numDebitCards == Constant.ZERO && numCreditCards > Constant.ZERO ->
                validateIfOnlyCreditCardsExists(debitCardState)
            else -> validateIfErrorStateExists(debitCardState, creditCardState)
        }
    }

    private fun validateIfOnlyDebitCardsExists(creditCardState: UiState): String {
        if (creditCardState in setOf(UiState.SUCCESS, UiState.EMPTY)) return AnalyticsConstant.DEBIT
        return AnalyticsConstant.DEBIT_CREDIT
    }

    private fun validateIfOnlyCreditCardsExists(debitCardState: UiState): String {
        if (debitCardState in setOf(UiState.SUCCESS, UiState.EMPTY)) return AnalyticsConstant.CREDIT
        return AnalyticsConstant.DEBIT_CREDIT
    }

    private fun validateIfErrorStateExists(
        debitCardState: UiState,
        creditCardState: UiState
    ): String {
        if ((debitCardState != UiState.ERROR && creditCardState == UiState.ERROR) ||
                (debitCardState == UiState.ERROR && creditCardState != UiState.ERROR))
            return AnalyticsConstant.DEBIT_CREDIT
        return AnalyticsConstant.HYPHEN_STRING
    }

    companion object {
        const val BANNER_ADD_TO_GOOGLE_WALLET = "banner-agregar-billetera-google-pay"
    }
}
