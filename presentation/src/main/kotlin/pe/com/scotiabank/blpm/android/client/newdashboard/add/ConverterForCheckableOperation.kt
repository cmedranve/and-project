package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.date.DateFormatter
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.twocolumntext.FactoryOfTwoColumnEntity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.model.RecentTransactionModel
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationRendering
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationUtil
import pe.com.scotiabank.blpm.android.ui.list.items.emptyUiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.AdapterFactoryOfTwoColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText
import java.lang.ref.WeakReference

class ConverterForCheckableOperation(
    private val weakResources: WeakReference<Resources?>,
    private val dividerPositions: List<Int>,
    private val paddingEntity: UiEntityOfPadding,
    private val dateFormatter: DateFormatter,
    private val currencyAmountFormatter: CurrencyFormatter,
    private val controller: ControllerOfMultipleSelection<RecentTransactionModel>,
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
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_2,
    )

    private val paddingOfIncludedItem = UiEntityOfPadding(
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_2,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_2,
    )

    private val factoryOfTwoColumnEntity = FactoryOfTwoColumnEntity()

    fun toUiEntityOfCheckBox(
        operation: RecentTransactionModel,
    ): UiEntityOfCheckableButton<RecentTransactionModel> {

        val amountWithSymbol: CharSequence = currencyAmountFormatter.format(
            currencyCode = operation.currency,
            amount = operation.amount ?: Constant.ZERO_DOUBLE,
        )

        val twoColumnBoldEntity = factoryOfTwoColumnEntity.create(
            paddingEntity = paddingOfIncludedTitleItem,
            appearance1 = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
            text1 = operation.title.orEmpty(),
            appearance2 = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
            text2 = amountWithSymbol,
            guidelinePercent = 0.66f,
        )

        val twoColumnTitleEntity = factoryOfTwoColumnEntity.create(
            paddingEntity = paddingOfIncludedItem,
            appearance1 = com.scotiabank.canvascore.R.style.canvascore_style_caption_alternate,
            text1 = createTitle(operation),
            appearance2 = com.scotiabank.canvascore.R.style.canvascore_style_caption_alternate,
            text2 = Constant.EMPTY_STRING,
            guidelinePercent = 1.0f,
        )

        val twoColumnSubtitleEntity = createTwoColumnForSubtitle(operation)
        val twoColumnTextCompound = UiCompound(
            uiEntities = listOf(twoColumnBoldEntity, twoColumnTitleEntity, twoColumnSubtitleEntity),
            factoryOfPortableAdapter = AdapterFactoryOfTwoColumnText(),
        )

        val compounds: List<UiCompound<*>> = listOf(twoColumnTextCompound)

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
            data = operation,
        )
    }

    private fun createTwoColumnForSubtitle(
        operation: RecentTransactionModel,
    ): UiEntityOfTwoColumnText {

        val dateFormatted: String = operation.paymentDate
            ?.let(dateFormatter::format)
            .orEmpty()

        return factoryOfTwoColumnEntity.create(
            paddingEntity = paddingOfIncludedItem,
            appearance1 = com.scotiabank.canvascore.R.style.canvascore_style_caption_alternate,
            text1 = weakResources.get()?.getString(R.string.my_list_paid_on, dateFormatted).orEmpty(),
            appearance2 = com.scotiabank.canvascore.R.style.canvascore_style_caption_alternate,
            text2 = Constant.EMPTY_STRING,
            guidelinePercent = 1.0f,
        )
    }

    private fun createTitle(operation: RecentTransactionModel): CharSequence = weakResources.get()
        ?.getString(
            R.string.my_list_add_checkable_payment_title,
            operation.serviceLabelPaymentCode,
            operation.paymentCode.orEmpty()
        )
        .orEmpty()

    private fun createDecorationCompounds(): List<DecorationCompound> {
        val decorationCompound = DecorationCompound(
            positions = dividerPositions,
            rendering = DecorationRendering(DecorationUtil::addDividerAboveEachItem),
        )
        return listOf(decorationCompound)
    }
}
