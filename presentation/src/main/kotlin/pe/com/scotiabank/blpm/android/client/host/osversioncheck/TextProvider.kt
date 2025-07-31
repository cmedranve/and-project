package pe.com.scotiabank.blpm.android.client.host.osversioncheck

import android.content.res.Resources
import android.text.SpannableStringBuilder
import java.lang.ref.WeakReference
import pe.com.scotiabank.blpm.android.client.R

class TextProvider(
    private val weakResources: WeakReference<Resources?>,
) {

    val descriptionForOsUpdate: SpannableStringBuilder by lazy {
        buildDescriptionForOsUpdate()
    }

    private fun buildDescriptionForOsUpdate(): SpannableStringBuilder {

        val fullText: String = weakResources.get()
            ?.getString(R.string.os_updater_message)
            .orEmpty()

        return SpannableStringBuilder.valueOf(fullText)
    }
}