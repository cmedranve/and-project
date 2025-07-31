package pe.com.scotiabank.blpm.android.client.base.session.entities

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class Currency(
    val id: String,
    val displayText: String,
    val symbol: String,
    val isTransactional: Boolean,
    val analyticsValue: String,
) {

    PEN(
        id = "PEN",
        displayText = "Soles",
        symbol = "S/",
        isTransactional = true,
        analyticsValue = "soles",
    ),
    USD(
        id = "USD",
        displayText = "Dólares",
        symbol = "US$",
        isTransactional = true,
        analyticsValue = "dolares",
    ),
    EUR(
        id = "EUR",
        displayText = "Euros",
        symbol = "€",
        isTransactional = false,
        analyticsValue = "euros",
    ),
    NONE(
        id = "NONE",
        displayText = Constant.EMPTY_STRING,
        symbol = Constant.EMPTY_STRING,
        isTransactional = false,
        analyticsValue = Constant.EMPTY_STRING,
    );

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): Currency = when (id?.uppercase()) {
            PEN.id -> PEN
            USD.id -> USD
            EUR.id -> EUR
            else -> NONE
        }

        @JvmStatic
        fun findTransactionalCurrencies(): List<Currency> = entries
            .filter(Currency::isTransactional)
    }
}
