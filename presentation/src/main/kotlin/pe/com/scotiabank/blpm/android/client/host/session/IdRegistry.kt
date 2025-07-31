package pe.com.scotiabank.blpm.android.client.host.session

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val modalIdOnLoggingOut: Long = randomLong(),
    val idOfGoToNotificationsSettings: Long = randomLong(),
)