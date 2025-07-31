package pe.com.scotiabank.blpm.android.client.base.carrier

import android.content.ActivityNotFoundException
import com.scotiabank.enhancements.uuid.randomLong

class CarrierOfActivityNotFound(
    val throwable: ActivityNotFoundException,
    val data: Any? = null,
    val id: Long = randomLong(),
)
