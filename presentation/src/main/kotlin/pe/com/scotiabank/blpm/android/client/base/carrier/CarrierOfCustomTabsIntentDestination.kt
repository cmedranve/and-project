package pe.com.scotiabank.blpm.android.client.base.carrier

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong

class CarrierOfCustomTabsIntentDestination(
    val uriDestination: Uri,
    val showTitle: Boolean = true,
    val instantAppsEnabled: Boolean = true,
    val shareState: Int = CustomTabsIntent.SHARE_STATE_OFF,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    val id: Long = randomLong(),
)
