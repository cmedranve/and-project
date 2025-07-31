package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val understoodButtonId: Long = randomLong(),
)
