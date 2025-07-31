package pe.com.scotiabank.blpm.android.client.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.UrlQuerySanitizer
import pe.com.scotiabank.blpm.android.client.base.approuting.BrowserEvent
import pe.com.scotiabank.blpm.android.client.base.network.UrlUtil
import java.lang.ref.WeakReference

class BrowserOpener(private val weakAppContext: WeakReference<out Context?>) {

    fun open(event: BrowserEvent, weakActivityContext: WeakReference<out Context?>) {
        val uri: Uri = sanitizeUrl(event.url) ?: return

        val intent = Intent(Intent.ACTION_VIEW)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = uri

        val isOpenedByActivity: Boolean = isOpenedByActivity(intent, weakActivityContext)
        if (isOpenedByActivity) return

        weakAppContext.get()?.startActivity(intent)
    }

    private fun isOpenedByActivity(
        intent: Intent,
        weakActivityContext: WeakReference<out Context?>
    ): Boolean {
        val activity: Context = weakActivityContext.get() ?: return false
        activity.startActivity(intent)
        return true
    }

    private fun sanitizeUrl(url: String): Uri? {
        val sanitizer = UrlQuerySanitizer.getUrlAndSpaceLegal()
        val sanitizedUrl = sanitizer.sanitize(url)
        val isValid: Boolean = UrlUtil.isValid(sanitizedUrl)
        if (!isValid) return null

        val uri = Uri.parse(sanitizedUrl)
        val isHostAllowed = uri.host == BROWSER_HOST
        if (!isHostAllowed) return null

        return uri
    }

    companion object {

        private const val BROWSER_HOST = "www.scotiabank.com.pe"
    }
}
