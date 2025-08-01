package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import android.content.res.Resources
import android.view.Gravity
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.labelbutton.FactoryOfLabelButtonEntity
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.twocolumntext.FactoryOfUpToTwoColumnEntity
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.CarrierOfFrequentOperation
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.IdentifiableTextButton
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationRendering
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationUtil
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.AdapterFactoryOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.emptyUiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.labelbuttonpair.AdapterFactoryOfLabelButtonPair
import pe.com.scotiabank.blpm.android.ui.list.items.labelbuttonpair.UiEntityOfLabelButtonPair
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.AdapterFactoryOfTwoColumnText
import java.lang.ref.WeakReference

class ConverterForEnabledPayment(
    weakResources: WeakReference<Resources?>,
    private val dividerPositions: List<Int>,
    private val paddingEntity: UiEntityOfPadding,
    private val formatter: CurrencyFormatter,
    receiver: InstanceReceiver,
    private val controller: ControllerOfMultipleSelection<FrequentOperationModel>,
) {

    private val emptyPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding()
    }

    private val paddingOfCheckBoxIcon: UiEntityOfPadding by lazy {
        UiEntityOfPadding(left = paddingEntity.left, top = paddingEntity.top)
    }

    private val paddingOfIncludedSide: UiEntityOfPadding by lazy {
        UiEntityOfPadding(right = paddingEntity.right, bottom = paddingEntity.bottom)
    }

    private val paddingOfIncludedTitleItem = UiEntityOfPadding(
        top = R.dimen.canvascore_margin_6,
        bottom = R.dimen.canvascore_margin_2,
    )

    private val paddingOfIncludedItem = UiEntityOfPadding(
        top = R.dimen.canvascore_margin_2,
        bottom = R.dimen.canvascore_margin_2,
    )

    private val emptyEndingEntity: UiEntityOfText by lazy {
        UiEntityOfText(
            appearance = R.style.canvascore_style_body2,
            gravity = Gravity.END,
            text = Constant.EMPTY_STRING,
        )
    }

    private val factoryOfLabelButtonEntity = FactoryOfLabelButtonEntity(receiver)
    private val factoryOfUpToTwoColumnEntity = FactoryOfUpToTwoColumnEntity()
    private val collectorToSeeMoreReceipts = CollectorToSeeMoreReceipts(
        receiver = receiver,
        weakResources = weakResources,
    )

    fun toUiEntityOfCheckBox(
        frequentOperation: FrequentOperationModel,
        id: Long,
    ): UiEntityOfCheckableButton<FrequentOperationModel> {

        val labelButtonPairEntity: UiEntityOfLabelButtonPair<CarrierOfFrequentOperation> = factoryOfLabelButtonEntity.create(
            paddingEntity = paddingOfIncludedTitleItem,
            labelAppearance = R.style.canvascore_style_subtitle2,
            labelText = frequentOperation.title,
            data = CarrierOfFrequentOperation(IdentifiableTextButton.SUB_MENU, frequentOperation),
            drawableEndId = pe.com.scotiabank.blpm.android.ui.R.drawable.ic_menu_kebab,
        )
        val labelButtonPairCompound = UiCompound(
            uiEntities = listOf(labelButtonPairEntity),
            factoryOfPortableAdapter = AdapterFactoryOfLabelButtonPair(),
        )

        val twoColumnSubtitleEntity = factoryOfUpToTwoColumnEntity.create(
            paddingEntity = paddingOfIncludedItem,
            appearance1 = R.style.canvascore_style_caption_alternate,
            text1 = frequentOperation.subTitle1,
            appearance2 = R.style.canvascore_style_caption_alternate,
            text2 = Constant.EMPTY_STRING,
            placeHolderEntityForEmptyText = emptyEndingEntity,
            guidelinePercent = 1.0f,
        )

        val amountWithSymbol: CharSequence = formatter.format(
            currencyCode = frequentOperation.currency,
            amount = frequentOperation.amount,
        )
        val twoColumnBoldAmountEntity = factoryOfUpToTwoColumnEntity.create(
            paddingEntity = paddingOfIncludedItem,
            appearance1 = R.style.canvascore_style_caption_alternate,
            text1 = frequentOperation.subTitle2,
            appearance2 = R.style.canvascore_style_subtitle2,
            text2 = amountWithSymbol,
            placeHolderEntityForEmptyText = emptyEndingEntity,
            guidelinePercent = 0.66f,
        )

        val twoColumnTextCompound = UiCompound(
            uiEntities = listOf(twoColumnSubtitleEntity, twoColumnBoldAmountEntity),
            factoryOfPortableAdapter = AdapterFactoryOfTwoColumnText(),
        )

        val textButtonCompound = UiCompound(
            uiEntities = collectorToSeeMoreReceipts.collect(frequentOperation),
            factoryOfPortableAdapter = AdapterFactoryOfTextButton(),
        )

        val compounds: List<UiCompound<*>> = listOf(
            labelButtonPairCompound,
            twoColumnTextCompound,
            textButtonCompound,
        )

        val sideRecyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingOfIncludedSide,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
            decorationCompounds = createDecorationCompounds(),
        )
        compounds.associateByTo(destination = sideRecyclerEntity.compoundsById, keySelector = ::byId)

        return UiEntityOfCheckableButton(
            paddingEntity = emptyPaddingEntity,
            paddingEntityOfCheckableIcon = paddingOfCheckBoxIcon,
            sideRecyclerEntity = sideRecyclerEntity,
            bottomRecyclerEntity = emptyUiEntityOfRecycler,
            controller = controller,
            data = frequentOperation,
            isEnabled = true,
            id = id,
        )
    }

    private fun createDecorationCompounds(): List<DecorationCompound> {
        val decorationCompound = DecorationCompound(
            positions = dividerPositions,
            rendering = DecorationRendering(DecorationUtil::addDividerAboveEachItem),
        )
        return listOf(decorationCompound)
    }
}