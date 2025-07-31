package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import android.content.res.Resources
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.ProxyOfClickableLink
import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.UiEntityOfAlertBanner
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference

class ConverterOfErrorAlertBanner(
    private val typefaceProvider: TypefaceProvider,
    private val weakResources: WeakReference<Resources?>,
    private val receiver: InstanceReceiver,
    private val paddingEntity: UiEntityOfPadding,
) {

    private val accessibilityMessage: String by lazy {
        weakResources.get()?.getString(R.string.accessibility_important_notice).orEmpty()
    }

    private fun notifyClick(action: Action) {
        receiver.receive(action)
    }

    fun toUiEntity(alertBannerInfo: AlertBannerInfo): UiEntityOfAlertBanner<Any> {

        val textContent: CharSequence = alertBannerInfo.createTextContent(
            weakResources = weakResources,
            typefaceProvider = typefaceProvider,
        )
        val callback = Runnable { notifyClick(alertBannerInfo.action) }
        val emphasisContent: CharSequence = alertBannerInfo.createEmphasisContent(
            weakResources = weakResources,
            typefaceProvider = typefaceProvider,
            clickableSpan = ProxyOfClickableLink(callback)
        )

        return UiEntityOfAlertBanner(
            paddingEntity = paddingEntity,
            textContent = textContent,
            iconContentDescription = accessibilityMessage,
            type = alertBannerInfo.type,
            emphasisContent = emphasisContent,
            supportLink = alertBannerInfo.supportLink,
            emphasisLink = alertBannerInfo.emphasisLink,
            data = alertBannerInfo,
            id = alertBannerInfo.id
        )
    }
}
