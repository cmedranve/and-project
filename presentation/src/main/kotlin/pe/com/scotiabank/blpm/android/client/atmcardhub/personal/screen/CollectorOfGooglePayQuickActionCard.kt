package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import android.text.SpannableStringBuilder
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.CollectorOfQuickActionCard
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.UiEntityOfChevron
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.UiEntityOfQuickActionCard
import java.lang.ref.WeakReference

class CollectorOfGooglePayQuickActionCard(
    private val weakResources: WeakReference<Resources?>,
    private val appModel: AppModel,
) : CollectorOfQuickActionCard<Any> {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?
    ): List<UiEntityOfQuickActionCard<Any>> {
        val entity = UiEntityOfQuickActionCard<Any>(
            paddingEntity = paddingEntity,
            iconRes = R.drawable.ic_google_wallet,
            description = createDescription(),
            receiver = receiver,
            chevronEntity = UiEntityOfChevron(
                iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_chevron_right_black
            )
        )
        return listOf(entity)
    }

    private fun createDescription(): SpannableStringBuilder {
        val fullText: String = weakResources.get()
            ?.getString(R.string.add_card_to_google_wallet).orEmpty()
        val shortText: String = weakResources.get()
            ?.getString(R.string.add_card_to_google_wallet_short_text).orEmpty()

        return SpannableStringBuilder
            .valueOf(fullText)
            .setTypefaceSpan(appModel.regularTypeface, fullText)
            .setTypefaceSpan(appModel.boldTypeface, shortText)
    }
}
