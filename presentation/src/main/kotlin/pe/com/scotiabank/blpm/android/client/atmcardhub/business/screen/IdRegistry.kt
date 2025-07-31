package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry (
    val idOfRetryHubButton: Long = randomLong(),
    val idOfGoToHomeButton: Long = randomLong(),
    val atmCardId: Long = randomLong(),
)
