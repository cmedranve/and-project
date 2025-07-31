package pe.com.scotiabank.blpm.android.client.base.carrier

import android.content.Intent
import android.net.Uri

class CarrierOfChooserDestination(
    val uriDestination: Uri,
    val mimeType: String,
    val action: String = Intent.ACTION_SEND,
)
