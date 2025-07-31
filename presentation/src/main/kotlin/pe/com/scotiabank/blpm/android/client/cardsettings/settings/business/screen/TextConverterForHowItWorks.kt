package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.content.Context
import android.text.SpannableStringBuilder
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.color.ColorUtil
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.emptySpannableStringBuilder
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setColorfulSpan
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class TextConverterForHowItWorks(
    private val typefaceProvider: TypefaceProvider,
    private val weakAppContext: WeakReference<Context?>,
    private val factory: FactoryOfOneColumnTextEntity,
    private val receiver: InstanceReceiver,
) {

    private val paddingEntityOfText: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = com.scotiabank.canvascore.R.dimen.canvascore_margin_9,
            right = com.scotiabank.canvascore.R.dimen.canvascore_margin_40,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
        )
    }

    fun toUiEntity(data: Setting): UiEntityOfOneColumnText {

        val descriptionBuilder: SpannableStringBuilder = create()

        return factory.create(
            paddingEntity = paddingEntityOfText,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            text = descriptionBuilder,
            receiver = receiver,
            data = data,
            id = data.info.cardId,
        )
    }

    fun create(): SpannableStringBuilder {

        val color: Int = weakAppContext.get()?.let(ColorUtil::getDarkBlueColor)
            ?: return emptySpannableStringBuilder

        val lightText: String = weakAppContext.get()
            ?.getString(R.string.card_settings_overdraft_description)
            .orEmpty()

        val textToBeBoldColoured: String = weakAppContext.get()
            ?.getString(R.string.card_settings_overdraft_how_does_it_work)
            .orEmpty()

        val boldText: CharSequence = SpannableStringBuilder
            .valueOf(textToBeBoldColoured)
            .setColorfulSpan(color, typefaceProvider.boldTypeface, textToBeBoldColoured)

        return SpannableStringBuilder
            .valueOf(lightText)
            .append(Constant.SPACE_WHITE)
            .append(boldText)
    }
}