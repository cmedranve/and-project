package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.base.number.DoubleParser
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared.TextSupplierForBuddyTip
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared.BuddyTipInfo
import pe.com.scotiabank.blpm.android.client.debitcard.pending.setting.ToolTipInfo
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.FormatterUtil
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.AdapterFactoryOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.BuddyTipType
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext.AdapterFactoryOfCurrencyEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext.UiEntityOfCurrencyEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.toggleswitch.AdapterFactoryOfToggleSwitch
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.toggleswitch.UiEntityOfToggleSwitch
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import java.lang.ref.WeakReference

class ConverterOfCardSetting(
    private val typefaceProvider: TypefaceProvider,
    private val atmCardType: AtmCardType,
    private val horizontalPaddingEntity: UiEntityOfPadding,
    private val filters: Array<InputFilter>,
    private val receiver: InstanceReceiver,
    private val weakAppContext: WeakReference<Context?>,
    private val factory: FactoryOfOneColumnTextEntity,
    private val doubleParser: DoubleParser,
    private val textConverterForHowItWorks: TextConverterForHowItWorks,
) {

    private val paddingEntityOfCard: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_10,
        )
    }

    private val horizontalPaddingEntityForContent: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = com.scotiabank.canvascore.R.dimen.canvascore_margin_9,
            right = com.scotiabank.canvascore.R.dimen.canvascore_margin_9,
        )
    }

    private val paddingEntityOfCardContent: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntityForContent.left,
            right = horizontalPaddingEntityForContent.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
        )
    }

    private val paddingEntityOfBuddyTip: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntityForContent.left,
            right = horizontalPaddingEntityForContent.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
        )
    }

    private val paddingEntityOfText: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntityForContent.left,
            right = com.scotiabank.canvascore.R.dimen.canvascore_margin_40,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
        )
    }

    private val emptyPadding: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    fun toUiEntity(
        setting: Setting,
        id: Long,
    ): UiEntityOfCard<Setting> = UiEntityOfCard(
        paddingEntity = paddingEntityOfCard,
        recyclerEntity = createRecyclerEntity(setting),
        useCompatPadding = true,
        strokeWidthRes = com.scotiabank.canvascore.R.dimen.canvascore_border_width_1,
        data = setting,
        id = id,
    )

    private fun createRecyclerEntity(setting: Setting): UiEntityOfRecycler {

        val toggleSwitchCompound = UiCompound(
            uiEntities = createToggleSwitchEntities(setting),
            factoryOfPortableAdapter = AdapterFactoryOfToggleSwitch(),
        )

        val oneColumnTextCompound = UiCompound(
            uiEntities = createOneColumnTextEntities(setting),
            factoryOfPortableAdapter = AdapterFactoryOfOneColumnText(),
        )

        val buddyTipCompound = UiCompound(
            uiEntities = createBuddyTipEntities(setting),
            factoryOfPortableAdapter = AdapterFactoryOfBuddyTip(),
        )

        val currencyEditTextCompound = UiCompound(
            uiEntities = createCurrencyEditTextsEntities(setting),
            factoryOfPortableAdapter = AdapterFactoryOfCurrencyEditText(),
        )

        val compounds: List<UiCompound<*>> = listOf(
            toggleSwitchCompound,
            oneColumnTextCompound,
            buddyTipCompound,
            currencyEditTextCompound,
        )

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfCardContent,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    private fun createToggleSwitchEntities(
        setting: Setting
    ): List<UiEntityOfToggleSwitch<Setting>> {

        val entity = UiEntityOfToggleSwitch(
            paddingEntity = emptyPadding,
            text = weakAppContext.get()?.getString(setting.info.titleResId).orEmpty(),
            receiver = receiver,
            data = setting,
            isChecked = setting.isCheckedForUi,
            isEnabled = setting.isEnabled,
            id = setting.info.switchId,
        )

        return listOf(entity)
    }

    private fun createOneColumnTextEntities(setting: Setting): List<UiEntityOfOneColumnText> {

        val isOverdraft: Boolean = setting.info.cardId == CardSettingInfo.OVERDRAFT.cardId
        if (isOverdraft) return createOneColumnTextEntitiesForOverdraft(setting)

        val descriptionResId: Int = setting.getDescriptionResId(type = atmCardType)
        val description = weakAppContext.get()?.getString(descriptionResId).orEmpty()

        val entity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityOfText,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            text = description,
            movementMethod = LinkMovementMethod.getInstance(),
            data = setting,
            id = setting.info.cardId,
        )
        return listOf(entity)
    }

    private fun createOneColumnTextEntitiesForOverdraft(
        setting: Setting,
    ): List<UiEntityOfOneColumnText> {
        val entity: UiEntityOfOneColumnText = textConverterForHowItWorks.toUiEntity(setting)
        return listOf(entity)
    }

    private fun createBuddyTipEntities(setting: Setting): List<UiEntityOfBuddyTip> {

        if (setting.isCheckedForUi.not()) return emptyList()

        val buddyTipInfo: BuddyTipInfo = setting.info.buddyTipInfo ?: return emptyList()

        val descriptionBuilder = createBuddyTipText(buddyTipInfo)
        
        val entity = UiEntityOfBuddyTip(
            paddingEntity = paddingEntityOfBuddyTip,
            iconRes = buddyTipInfo.iconRes,
            descriptionBuilder = descriptionBuilder,
            expandedDescriptionBuilder = descriptionBuilder,
            type = BuddyTipType.EXPANDABLE,
            receiver = receiver
        )

        return listOf(entity)
    }

    private fun createBuddyTipText(buddyTipInfo: BuddyTipInfo): SpannableStringBuilder {
        
        val textSupplier = TextSupplierForBuddyTip(
            typefaceProvider = typefaceProvider,
            weakAppContext = weakAppContext,
            buddyTipInfo = buddyTipInfo,
        )

        return SpannableStringBuilder.valueOf(textSupplier.get())
    }

    private fun createCurrencyEditTextsEntities(
        setting: Setting
    ): List<UiEntityOfCurrencyEditText<EditableLimit>> {

        if (setting.isCheckedForUi.not()) return emptyList()

        return setting
            .editableLimits
            .map(::toCurrencyEditText)
    }

    private fun toCurrencyEditText(
        limit: EditableLimit,
    ): UiEntityOfCurrencyEditText<EditableLimit> {

        val info: CardLimitInfo = limit.info
        val toolTipEntity: UiEntityOfToolTip? = info.toolTipInfo?.let(::toUiEntityOfToolTip)

        val entity = UiEntityOfCurrencyEditText(
            paddingEntity = paddingEntityOfBuddyTip,
            titleText = weakAppContext.get()?.getString(info.titleResId).orEmpty(),
            currencyText = info.currency.symbol,
            hintText = weakAppContext.get()?.getString(info.hintResId).orEmpty(),
            receiver = receiver,
            filters = filters,
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            toolTipEntity = toolTipEntity,
            data = limit,
            isEnabled = limit.isEnabled,
            id = limit.id,
        )
        val formattedAmount: String = FormatterUtil.format(limit.amount.toString(), false)
        entity.text = formattedAmount
        entity.errorText = getErrorText(limit)
        entity.supplementaryText = getSupplementaryText(limit)

        return entity
    }

    private fun toUiEntityOfToolTip(toolTipInfo: ToolTipInfo): UiEntityOfToolTip = UiEntityOfToolTip(
        accessibilityText = weakAppContext.get()?.getString(toolTipInfo.accessibilityTextRes).orEmpty(),
        headlineText = weakAppContext.get()?.getString(toolTipInfo.headlineTextRes).orEmpty(),
        contentText = weakAppContext.get()?.getString(toolTipInfo.contentTextRes).orEmpty(),
        buttonText = weakAppContext.get()?.getString(toolTipInfo.buttonTextRes).orEmpty(),
        id = toolTipInfo.id,
    )

    private fun getErrorText(limit: EditableLimit): CharSequence {
        if (limit.isAllowed()) return Constant.EMPTY_STRING
        return limit.createErrorText(doubleParser.numberFormat)
    }

    private fun getSupplementaryText(limit: EditableLimit): CharSequence {
        if (limit.isAllowed()) return limit.createSupplementaryText(doubleParser.numberFormat)
        return Constant.EMPTY_STRING
    }
}