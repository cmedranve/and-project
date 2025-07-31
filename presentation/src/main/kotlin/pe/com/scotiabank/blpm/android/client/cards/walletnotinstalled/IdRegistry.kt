package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val findButtonPrimaryId: Long = randomLong(),
    val findButtonSecondaryId: Long = randomLong(),
)
