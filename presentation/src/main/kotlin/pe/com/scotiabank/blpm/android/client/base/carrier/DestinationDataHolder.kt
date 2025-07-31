package pe.com.scotiabank.blpm.android.client.base.carrier

import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong

class DestinationDataHolder(
    val receiver: InstanceReceiver,
    val data: Any? = null,
    val id: Long = randomLong(),
)
