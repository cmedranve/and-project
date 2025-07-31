package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import android.content.Context
import android.text.SpannableStringBuilder
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.IdRegistry
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference
import kotlin.time.Duration

class ConverterOfTimerBuddyTip(
    private val typefaceProvider: TypefaceProvider,
    private val weakAppContext: WeakReference<Context?>,
    private val receiver: InstanceReceiver,
    private val idRegistry: IdRegistry,
    private val paddingEntity: UiEntityOfPadding,
) {

    fun toUiEntity(timerInfo: TimerInfo , duration: Duration?): UiEntityOfBuddyTip {

        val descriptionBuilder: SpannableStringBuilder = timerInfo.createDescriptionBuilder(
            weakAppContext = weakAppContext,
            typefaceProvider = typefaceProvider,
            duration = duration,
        )

        return UiEntityOfBuddyTip(
            paddingEntity = paddingEntity,
            iconRes = com.scotiabank.icons.illustrative.R.drawable.ic_security_outlined_multicoloured_36,
            descriptionBuilder = descriptionBuilder,
            expandedDescriptionBuilder = descriptionBuilder,
            type = timerInfo.type,
            receiver = receiver,
            id = idRegistry.timerBuddyTipId,
        )
    }
}
