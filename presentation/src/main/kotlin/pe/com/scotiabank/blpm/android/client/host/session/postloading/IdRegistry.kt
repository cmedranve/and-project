package pe.com.scotiabank.blpm.android.client.host.session.postloading

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val loadingId: Long = randomLong(),
)