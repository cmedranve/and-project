package pe.com.scotiabank.blpm.android.client.base.carrier

import android.content.Intent
import android.net.Uri
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY

class CarrierOfActionDestination private constructor(
    val dataHolder: DestinationDataHolder?,
    val receiver: InstanceReceiver?,
    val data: Any?,
    val id: Long,
    val extras: Map<String, String>,
) {

    var uriDestination: Uri? = null
        private set
    var action: String = String.EMPTY
        private set

    @JvmOverloads
    constructor(
        uriDestination: Uri,
        action: String = Intent.ACTION_VIEW,
        dataHolder: DestinationDataHolder? = null,
        receiver: InstanceReceiver? = null,
        data: Any? = null,
        id: Long = randomLong(),
        extras: Map<String, String> = emptyMap()
    ) : this(
        dataHolder = dataHolder,
        receiver = receiver,
        data = data,
        id = id,
        extras = extras,
    ) {
        this.uriDestination = uriDestination
        this.action = action
    }

    @JvmOverloads
    constructor(
        action: String,
        dataHolder: DestinationDataHolder? = null,
        receiver: InstanceReceiver? = null,
        data: Any? = null,
        id: Long = randomLong(),
        extras: Map<String, String> = emptyMap()
    ) : this(
        dataHolder = dataHolder,
        receiver = receiver,
        data = data,
        id = id,
        extras = extras,
    ) {
        this.action = action
    }
}
