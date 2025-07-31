package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import pe.com.scotiabank.blpm.android.client.debitcard.DebitCard

interface CardDebitHubService {

    fun add(debitCard: DebitCard)

    fun clear()
}
