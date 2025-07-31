package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.Context
import android.content.res.Resources
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.color.ColorUtil
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.emptySpannableStringBuilder
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setColorfulSpan
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.AdapterFactoryOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.BuddyTipType
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.AdapterFactoryOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import java.lang.ref.WeakReference

class CardConverterOfAtmCard(
    private val typefaceProvider: TypefaceProvider,
    private val weakResources: WeakReference<Resources?>,
    private val weakAppContext: WeakReference<Context?>,
    private val paddingEntity: UiEntityOfPadding,
    private val textConverterForActive: TextConverterOfAtmCard,
    private val textConverterForLocked: TextConverterOfAtmCard,
    private val converterOfCardInfo: ConverterOfCardInfo,
    private val receiver: InstanceReceiver,
) {

    private val paddingEntityOfBuddyTip: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_22,
            left = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            right = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
        )
    }

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    fun toUiEntity(card: Card): UiEntityOfCard<Any> {
        val isActive: Boolean = AtmCardStatus.ACTIVE == card.status
        val recyclerEntity = if (isActive) createEntityForActive(card) else createEntityForLocked(card)
        return UiEntityOfCard(
            paddingEntity = paddingEntity,
            recyclerEntity = recyclerEntity,
            useCompatPadding = false,
        )
    }

    private fun createEntityForActive(card: Card): UiEntityOfRecycler {

        val doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any> = converterOfCardInfo.toUiEntity(
            cardIcon = getBrandActive(card),
            oneColumnTextEntities = textConverterForActive.toUiEntities(card),
            rightDrawableId = com.scotiabank.canvascore.R.drawable.canvascore_icon_chevron_right_blue,
            data = card,
            isClickable = true,
        )
        val doubleEndedImageCompound = UiCompound(
            uiEntities = listOf(doubleEndedImageEntity),
            factoryOfPortableAdapter = AdapterFactoryOfDoubleEndedImage(),
        )

        val buddyTipCompound = UiCompound(
            uiEntities = emptyList(),
            factoryOfPortableAdapter = AdapterFactoryOfBuddyTip(),
        )

        val compounds: List<UiCompound<*>> = listOf(doubleEndedImageCompound, buddyTipCompound)

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = emptyPaddingEntity,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    @DrawableRes
    private fun getBrandActive(card: Card): Int {
        if (card.brand == AtmCardBrand.VISA) return R.drawable.ic_visa
        return R.drawable.ic_mc
    }

    private fun createEntityForLocked(card: Card): UiEntityOfRecycler {

        val doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any> = converterOfCardInfo.toUiEntity(
            cardIcon = getBrandLocked(card),
            oneColumnTextEntities = textConverterForLocked.toUiEntities(card),
            rightDrawableId = ResourcesCompat.ID_NULL,
            data = card,
            isClickable = false,
        )
        val doubleEndedImageCompound = UiCompound(
            uiEntities = listOf(doubleEndedImageEntity),
            factoryOfPortableAdapter = AdapterFactoryOfDoubleEndedImage(),
        )

        val buddyTipEntity: UiEntityOfBuddyTip = createBuddyTipEntity(card)
        val buddyTipCompound = UiCompound(
            uiEntities = listOf(buddyTipEntity),
            factoryOfPortableAdapter = AdapterFactoryOfBuddyTip(),
        )

        val compounds: List<UiCompound<*>> = listOf(doubleEndedImageCompound, buddyTipCompound)

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = emptyPaddingEntity,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    @DrawableRes
    private fun getBrandLocked(card: Card): Int {
        if (card.brand == AtmCardBrand.VISA) return R.drawable.ic_visa_disabled
        return R.drawable.ic_mc_disabled
    }

    private fun createBuddyTipEntity(card: Card): UiEntityOfBuddyTip {

        val descriptionBuilder: SpannableStringBuilder = createFrom(card.status.nameFromNetworkCall)

        return UiEntityOfBuddyTip(
            paddingEntity = paddingEntityOfBuddyTip,
            iconRes = com.scotiabank.icons.illustrative.R.drawable.ic_security_outlined_multicoloured_30,
            descriptionBuilder = descriptionBuilder,
            expandedDescriptionBuilder = descriptionBuilder,
            type = BuddyTipType.EXPANDABLE,
            receiver = receiver,
            data = CardSettingAction.CALL_NOW,
        )
    }

    private fun createFrom(statusDescription: String): SpannableStringBuilder {

        @ColorInt val color: Int = weakAppContext.get()?.let(ColorUtil::getDarkBlueColor)
            ?: return emptySpannableStringBuilder

        val textToBeBoldColoured: CharSequence = weakResources.get()
            ?.getString(CardSettingAction.CALL_NOW.labelRes)
            .orEmpty()

        val boldColouredText: CharSequence = SpannableStringBuilder
            .valueOf(textToBeBoldColoured)
            .setColorfulSpan(color, typefaceProvider.boldTypeface, textToBeBoldColoured)

        val forFurtherDetails: String = weakResources.get()
            ?.getString(R.string.cards_settings_lock_description)
            .orEmpty()

        val boldStatusDescription: CharSequence = SpannableStringBuilder
            .valueOf(statusDescription)
            .setTypefaceSpan(typefaceProvider.boldTypeface, statusDescription)

        return SpannableStringBuilder
            .valueOf(boldStatusDescription)
            .append(Constant.DOT)
            .append(Constant.SPACE_WHITE)
            .append(forFurtherDetails)
            .append(Constant.SPACE_WHITE)
            .append(boldColouredText)
    }
}
