package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo

interface CardService {

    fun clearThenAdd(cards: List<AtmCardInfo>)
}
