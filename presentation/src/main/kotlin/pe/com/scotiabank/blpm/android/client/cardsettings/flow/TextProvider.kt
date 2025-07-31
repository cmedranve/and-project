package pe.com.scotiabank.blpm.android.client.cardsettings.flow

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import java.lang.ref.WeakReference

class TextProvider(private val weakResources: WeakReference<Resources?>) {

    val informationScreenTitle: String by lazy {
        weakResources.get()?.getString(R.string.card_settings_why_warn_travel).orEmpty()
    }

    val informationScreenMessage: String by lazy {
        weakResources.get()?.getString(R.string.card_settings_why_warn_travel_message).orEmpty()
    }
}
