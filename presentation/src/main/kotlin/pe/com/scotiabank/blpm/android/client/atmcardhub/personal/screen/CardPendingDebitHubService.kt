package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import pe.com.scotiabank.blpm.android.client.debitcard.PendingCard

interface CardPendingDebitHubService {
    fun addPendingCard(pendingCard: PendingCard)

    fun fetchAll(): List<PendingCard>

    fun removePendingCard()
}
