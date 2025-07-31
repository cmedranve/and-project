package pe.com.scotiabank.blpm.android.client.dashboard.home

import android.content.res.Resources
import android.text.SpannableStringBuilder
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.UiEntityOfQuickActionCard
import java.lang.ref.WeakReference

class ConverterForGateProduct(
    private val appModel: AppModel,
    private val paddingEntity: UiEntityOfPadding,
    private val receiver: InstanceReceiver,
    private val weakResources: WeakReference<Resources?>,
) {

    private val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(Constant.EMPTY_STRING)

    fun toUiEntityOfQuickActionCard(
        data: GateProduct,
    ): UiEntityOfQuickActionCard<GateProduct> = UiEntityOfQuickActionCard(
        paddingEntity = paddingEntity,
        iconRes = data.idRes,
        description = buildDescription(data),
        receiver = receiver,
        data = data,
    )

    private fun buildDescription(data: GateProduct): SpannableStringBuilder {
        val rawLightText: CharSequence = weakResources.get()
            ?.getString(data.description)
            ?: return empty

        val lightText: CharSequence = SpannableStringBuilder
            .valueOf(rawLightText)
            .setTypefaceSpan(appModel.regularTypeface, rawLightText)

        return SpannableStringBuilder.valueOf(lightText)
    }
}