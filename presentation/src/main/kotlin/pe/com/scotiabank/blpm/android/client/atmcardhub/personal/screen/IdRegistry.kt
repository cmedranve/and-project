package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val atmCardId: Long = randomLong(),
)
