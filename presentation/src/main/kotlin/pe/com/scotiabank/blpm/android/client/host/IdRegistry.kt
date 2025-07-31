package pe.com.scotiabank.blpm.android.client.host

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val callMarketApp: Long = randomLong(),
    val callMarketUrl: Long = randomLong(),
)