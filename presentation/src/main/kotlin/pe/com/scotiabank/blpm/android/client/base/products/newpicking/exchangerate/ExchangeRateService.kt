package pe.com.scotiabank.blpm.android.client.base.products.newpicking.exchangerate

import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency

interface ExchangeRateService {

    fun clearThenAddExchangeRate(originCurrency: Currency)
}
