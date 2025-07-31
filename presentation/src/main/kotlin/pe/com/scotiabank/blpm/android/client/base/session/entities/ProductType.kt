package pe.com.scotiabank.blpm.android.client.base.session.entities

enum class ProductType(val id: String, val displayText: String) {

    GUARANTEE_ACCOUNT(id = "GT", displayText = "Cuenta Garantía"),
    SAVING_ACCOUNT(id = "AH", displayText = "Cuenta Ahorro"),
    CURRENT_ACCOUNT(id = "CC", displayText = "Cuenta Corriente"),
    CTS_ACCOUNT(id = "CTS", displayText = "Cuenta CTS"),
    TIME_DEPOSIT(id = "DEP", displayText = "Depósito"),
    LOAN(id = "PR", displayText = "Préstamo"),
    LOAN_WANTING(id = "QP", displayText = "Quiero un Préstamo"),
    CREDIT_CARD(id = "TC", displayText = "Tarjeta de Crédito"),
    CASH_ADVANCE(id = "XL", displayText = "InstaCash"),
    MUTUAL_FUNDS(id = "FM", displayText = "Fondos Mutuos"),
    GOAL(id = "GL", displayText = "Metas"),
    PAGUM(id = "PG", displayText = "Pagum"),
    SCOTIA_BOLSA(id = "SB", displayText = "Scotia Bolsa"),
    ACCOUNT(id = "UNK", displayText = "Cuenta"),;

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): ProductType = when (id?.uppercase()) {
            GUARANTEE_ACCOUNT.id -> GUARANTEE_ACCOUNT
            SAVING_ACCOUNT.id -> SAVING_ACCOUNT
            CURRENT_ACCOUNT.id -> CURRENT_ACCOUNT
            CTS_ACCOUNT.id -> CTS_ACCOUNT
            TIME_DEPOSIT.id -> TIME_DEPOSIT
            LOAN.id -> LOAN
            LOAN_WANTING.id -> LOAN_WANTING
            CREDIT_CARD.id -> CREDIT_CARD
            CASH_ADVANCE.id -> CASH_ADVANCE
            MUTUAL_FUNDS.id -> MUTUAL_FUNDS
            GOAL.id -> GOAL
            PAGUM.id -> PAGUM
            SCOTIA_BOLSA.id -> SCOTIA_BOLSA
            else -> ACCOUNT
        }
    }
}
