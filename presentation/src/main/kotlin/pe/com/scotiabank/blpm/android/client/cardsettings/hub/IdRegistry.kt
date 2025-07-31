package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val retryButtonId: Long = randomLong(),
    val cardGroupId: Long = randomLong(),
)
