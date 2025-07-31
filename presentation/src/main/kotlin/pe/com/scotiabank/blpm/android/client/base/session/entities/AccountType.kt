package pe.com.scotiabank.blpm.android.client.base.session.entities

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class AccountType(val id: String, val displayText: String, val currency: Currency) {

    PEN_SAVING(id = "AHMN", displayText = "Ahorro Soles", currency = Currency.PEN),
    USD_SAVING(id = "AHME", displayText = "Ahorro Dólares", currency = Currency.USD),
    EUR_SAVING(id = "AHEUR", displayText = "Ahorro Euros", currency = Currency.EUR),
    PEN_CURRENT(id = "CCMN", displayText = "Cta. Cte. Soles", currency = Currency.PEN),
    USD_CURRENT(id = "CCME", displayText = "Cta. Cte. Dólares", currency = Currency.USD),
    EUR_CURRENT(id = "CCEUR", displayText = "Cta. Cte. Euros", currency = Currency.EUR),
    PEN_CTS(id = "CTSMN", displayText = "CTS Soles", currency = Currency.PEN),
    USD_CTS(id = "CTSME", displayText = "CTS Dólares", currency = Currency.USD),
    NONE(id = "NONE", displayText = Constant.EMPTY_STRING, currency = Currency.NONE);

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): AccountType = when (id?.uppercase()) {
            PEN_SAVING.id -> PEN_SAVING
            USD_SAVING.id -> USD_SAVING
            EUR_SAVING.id -> EUR_SAVING
            PEN_CURRENT.id -> PEN_CURRENT
            USD_CURRENT.id -> USD_CURRENT
            EUR_CURRENT.id -> EUR_CURRENT
            PEN_CTS.id -> PEN_CTS
            USD_CTS.id -> USD_CTS
            else -> NONE
        }

        @JvmStatic
        fun findTransactionalOnes(): List<AccountType> = entries
            .filter(::isTransactional)
            .toList()

        @JvmStatic
        private fun isTransactional(accountType: AccountType): Boolean {
            val idOfAccountType: String = accountType.id
            val idOfCtsAccountType: String = ProductType.CTS_ACCOUNT.id

            val isCts: Boolean = idOfAccountType.startsWith(idOfCtsAccountType)
            if (isCts) return false

            return accountType.currency.isTransactional
        }
    }
}
