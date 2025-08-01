package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.util.Predicate
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.TextCollectorForDisabledOrErrorState
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.quantitytext.TextBuilderForQuantity
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment.ComposerOfCheckablePayment
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment.ComposerOfSuccessfulPayment
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment.CompositeForPaymentSection
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment.LabelButtonComposerForSuccessState
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment.SkeletonCollectorForPaymentLabelButton
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment.TextButtonComposerForEmptyState
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment.TextCollectorForPaymentEmptyState
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.sectionloading.CollectorOfHorizontalSkeletonList
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.sectionloading.SkeletonCollectorForSection
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary.CollectorOfFrequentOperationType
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary.ComposerOfFrequentOperationType
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary.SkeletonCollectorForSummary
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary.TextCollectorForSummaryEmptyState
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.transfer.ComposerOfCheckableTransfer
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.transfer.CompositeForTransferSection
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.transfer.SkeletonCollectorForTransferTitle
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.transfer.TextCollectorForTransferEmptyState
import pe.com.scotiabank.blpm.android.client.base.quantitytext.TextComposerForQuantity
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.transfer.otherbank.form.CollectorOfUnavailableImmediate
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.ComposerOfAlertBanner
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading.CanvasButtonLoadingController
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading.ComposerOfCanvasButtonLoading
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.ComposerOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended.ComposerOfDoubleEndedSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopCompositeForEnabled private constructor(
    dispatcherProvider: DispatcherProvider,
    private val skeletonComposerForSummary: ComposerOfRecycler,
    private val imagePaddingEntityForSummaryErrorState: UiEntityOfPadding,
    private val imagePaddingEntityForSummaryEmptyState: UiEntityOfPadding,
    private val imageComposerForSummaryErrorState: ComposerOfOneColumnImage,
    private val textComposerForSummaryErrorState: ComposerOfOneColumnText,
    private val buttonComposerForSummaryErrorState: ComposerOfCanvasButtonLoading,
    private val imageComposerForSummaryEmptyState: ComposerOfOneColumnImage,
    private val textComposerForSummaryEmptyState: ComposerOfOneColumnText,
    private val composerOfFrequentOperationType: ComposerOfFrequentOperationType,
    private val skeletonComposerForTransferTitle: ComposerOfSkeleton,
    private val skeletonComposerForPaymentLabelButton: ComposerOfRecycler,
    private val skeletonComposerForSection: ComposerOfDoubleEndedSkeleton,
    private val compositeForTransferSection: CompositeForTransferSection,
    private val compositeForPaymentSection: CompositeForPaymentSection,
    override var currentState: UiState = UiState.BLANK,
): DispatcherProvider by dispatcherProvider,
    HolderOfImmediateAvailability by compositeForTransferSection,
    CanvasButtonLoadingController by buttonComposerForSummaryErrorState,
    UiStateHolder
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    val controllerOfFrequentOperationType: SelectionControllerOfChipsComponent<FrequentOperationType> by composerOfFrequentOperationType::controller
    val chipEntitiesByFrequentOperationTypeText: Map<String, UiEntityOfChip<FrequentOperationType>> by composerOfFrequentOperationType::chipEntitiesByText

    val selectedFrequentOperationType: FrequentOperationType?
        get() = composerOfFrequentOperationType.selectedType

    val compositeByTypeId: Map<Long, CompositeForOperationTypeSection?> = mapOf(
        FrequentOperationType.TRANSFER.id to compositeForTransferSection,
        FrequentOperationType.PAYMENT.id to compositeForPaymentSection,
    )

    private val isLoadingVisibleForSummaryOrTransfer: Boolean
        get() = isLoadingVisible || compositeForTransferSection.isLoadingVisible

    private val compositeForOperationTypeSection: CompositeForOperationTypeSection?
        get() = selectedFrequentOperationType
            ?.id
            ?.let(compositeByTypeId::get)

    private val isLoadingVisibleForSummaryOrSection: Boolean
        get() = isLoadingVisible || compositeForOperationTypeSection?.isLoadingVisible == true

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val skeletonCompoundForSummary = skeletonComposerForSummary.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible),
        )

        val imageCompoundForSummaryErrorState = imageComposerForSummaryErrorState.composeUiData(
            paddingEntity = imagePaddingEntityForSummaryErrorState,
            visibilitySupplier = Supplier(::isErrorVisible),
        )

        val textCompoundForSummaryErrorState = textComposerForSummaryErrorState.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible),
        )

        val buttonCompoundForSummaryErrorState = buttonComposerForSummaryErrorState.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible),
        )

        val imageCompoundForSummaryEmptyState = imageComposerForSummaryEmptyState.composeUiData(
            paddingEntity = imagePaddingEntityForSummaryEmptyState,
            visibilitySupplier = Supplier(::isEmptyVisible),
        )

        val textCompoundForSummaryEmptyState = textComposerForSummaryEmptyState.composeUiData(
            visibilitySupplier = Supplier(::isEmptyVisible),
        )

        val frequentOperationTypeCompound = composerOfFrequentOperationType.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        val skeletonCompoundForTransferTitle = skeletonComposerForTransferTitle.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisibleForSummaryOrTransfer),
        )

        val skeletonCompoundForPaymentLabelButton = skeletonComposerForPaymentLabelButton.composeUiData(
            visibilitySupplier = Supplier(compositeForPaymentSection::isLoadingVisible),
        )

        val skeletonCompoundForSection = skeletonComposerForSection.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisibleForSummaryOrSection),
        )

        val mutableCompounds: MutableList<UiCompound<*>> = mutableListOf(
            skeletonCompoundForSummary,
            imageCompoundForSummaryErrorState,
            textCompoundForSummaryErrorState,
            buttonCompoundForSummaryErrorState,
            imageCompoundForSummaryEmptyState,
            textCompoundForSummaryEmptyState,
            frequentOperationTypeCompound,
            skeletonCompoundForTransferTitle,
            skeletonCompoundForPaymentLabelButton,
            skeletonCompoundForSection,
        )

        val transferSectionCompounds = compositeForTransferSection.compose()
        mutableCompounds.addAll(transferSectionCompounds)

        val paymentSectionCompounds = compositeForPaymentSection.compose()
        mutableCompounds.addAll(paymentSectionCompounds)

        return mutableCompounds
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val appModel: AppModel,
        private val weakResources: WeakReference<Resources?>,
        private val templateForAddingRecentPayments: OptionTemplate,
        private val frequentOperationTypes: Collection<FrequentOperationType>,
        private val idRegistry: IdRegistry,
        private val formatter: CurrencyFormatter,
        private val matcher: OperationMatcher,
    ) {

        private val dividerPositions: List<Int> by lazy {
            val firstIndex = 0
            listOf(firstIndex)
        }

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            )
        }

        private val paddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val imagePaddingEntityForSummaryErrorState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_48,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val buttonPaddingEntityForErrorState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_36,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_36,
            )
        }

        private val imagePaddingEntityForSummaryEmptyState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_32,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val leftSkeletonPaddingEntityForSectionContent: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                left = horizontalPaddingEntity.left,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            )
        }

        private val imagePaddingEntityForSectionErrorState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_32,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }


        private val paddingEntityForSuccessfulState: UiEntityOfPadding = UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_14,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_14,
        )

        private val imagePaddingEntityForSuccessfulPayment: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_14,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            )
        }

        fun create(receiver: InstanceReceiver): MainTopCompositeForEnabled {

            val skeletonCollectorForSummary = SkeletonCollectorForSummary(horizontalPaddingEntity)
            val skeletonCollectorForPaymentLabelButton = SkeletonCollectorForPaymentLabelButton(paddingEntity)

            val imageCollectorForErrorState = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)

            val textCollectorForErrorState = createTextCollectorForDisabledOrErrorState(
                titleRes = R.string.my_list_error_list_title,
                descriptionRes = R.string.my_list_error_list_description,
            )

            val imageCollectorForEmptyState = SingleCollectorOfOneColumnImage(R.drawable.ic_my_list_empty)
            val textCollectorForEmptyState = TextCollectorForSummaryEmptyState(horizontalPaddingEntity, appModel, weakResources)

            val collectorOfFrequentOperationType = CollectorOfFrequentOperationType(horizontalPaddingEntity, frequentOperationTypes)

            val composerOfFrequentOperationType = ComposerOfFrequentOperationType(
                collector = collectorOfFrequentOperationType,
                receiver = receiver,
            )

            val visibilityPredicate: Predicate<FrequentOperationType> = composerOfFrequentOperationType.visibilityPredicate

            val composite = MainTopCompositeForEnabled(
                dispatcherProvider = dispatcherProvider,
                skeletonComposerForSummary = ComposerOfRecycler(skeletonCollectorForSummary),
                imagePaddingEntityForSummaryErrorState = imagePaddingEntityForSummaryErrorState,
                imagePaddingEntityForSummaryEmptyState = imagePaddingEntityForSummaryEmptyState,
                imageComposerForSummaryErrorState = ComposerOfOneColumnImage(imageCollectorForErrorState, receiver),
                textComposerForSummaryErrorState = ComposerOfOneColumnText(textCollectorForErrorState),
                buttonComposerForSummaryErrorState = ComposerOfCanvasButtonLoading(buttonPaddingEntityForErrorState, receiver),
                imageComposerForSummaryEmptyState = ComposerOfOneColumnImage(imageCollectorForEmptyState, receiver),
                textComposerForSummaryEmptyState = ComposerOfOneColumnText(textCollectorForEmptyState),
                composerOfFrequentOperationType = composerOfFrequentOperationType,
                skeletonComposerForTransferTitle = createSkeletonComposerForTransferSectionTitle(),
                skeletonComposerForPaymentLabelButton = ComposerOfRecycler(skeletonCollectorForPaymentLabelButton),
                skeletonComposerForSection = createSkeletonComposerForOperationTypeSection(),
                compositeForTransferSection = createCompositeForTransferSection(visibilityPredicate, receiver),
                compositeForPaymentSection = createCompositeForPaymentSection(visibilityPredicate, receiver),
            )

            composite.addForCanvasButtonLoading(
                id = idRegistry.buttonIdOfSummaryRetry,
                isEnabled = true,
                text = weakResources.get()?.getString(R.string.try_again).orEmpty(),
            )

            return composite
        }

        private fun createTextCollectorForDisabledOrErrorState(
            @StringRes titleRes: Int,
            @StringRes descriptionRes: Int,
        ) = TextCollectorForDisabledOrErrorState(
            paddingEntity = paddingEntity,
            weakResources = weakResources,
            titleRes = titleRes,
            descriptionRes = descriptionRes,
        )

        private fun createSkeletonComposerForTransferSectionTitle() = ComposerOfSkeleton(
            collector = SkeletonCollectorForTransferTitle(paddingEntity),
        )

        private fun createSkeletonComposerForOperationTypeSection() = ComposerOfDoubleEndedSkeleton(
            collector = createSkeletonCollectorForSectionContent(),
        )

        private fun createSkeletonCollectorForSectionContent() = SkeletonCollectorForSection(
            dividerPositions = dividerPositions,
            paddingEntity = paddingEntity,
            paddingOfLeftSkeleton = leftSkeletonPaddingEntityForSectionContent,
            collectorOfHorizontalSkeletonList = CollectorOfHorizontalSkeletonList(),
        )
        private fun createCompositeForTransferSection(
            visibilityPredicate: Predicate<FrequentOperationType>,
            receiver: InstanceReceiver,
            type: FrequentOperationType = FrequentOperationType.TRANSFER,
        ): CompositeForTransferSection {

            val imageCollectorForDisabledOrErrorState = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)

            val textCollectorForDisabledState = createTextCollectorForDisabledOrErrorState(
                titleRes = R.string.my_list_disabled_transfer_list_title,
                descriptionRes = R.string.my_list_disabled_transfer_list_description,
            )

            val textCollectorForErrorState = createTextCollectorForDisabledOrErrorState(
                titleRes = R.string.my_list_error_transfer_list_title,
                descriptionRes = R.string.my_list_error_transfer_list_description,
            )

            val textCollectorForEmptyState = TextCollectorForTransferEmptyState(horizontalPaddingEntity, weakResources)

            val composite = CompositeForTransferSection(
                paddingEntity = paddingEntity,
                imagePaddingEntityForDisabledOrErrorState = imagePaddingEntityForSectionErrorState,
                imageComposerForDisabledOrErrorState = ComposerOfOneColumnImage(imageCollectorForDisabledOrErrorState, receiver),
                textComposerForDisabledState = ComposerOfOneColumnText(textCollectorForDisabledState),
                textComposerForErrorState = ComposerOfOneColumnText(textCollectorForErrorState),
                buttonComposerForErrorState = ComposerOfCanvasButtonLoading(buttonPaddingEntityForErrorState, receiver),
                textComposerForEmptyState = ComposerOfOneColumnText(textCollectorForEmptyState),
                textComposerForQuantity = createTextComposerForSuccessState(type),
                composerOfUnavailableImmediate = createComposerOfUnavailableImmediate(receiver),
                composerOfCheckableTransfer = createComposerOfCheckableTransfer(receiver),
                visibilityPredicate = visibilityPredicate,
                frequentOperationType = type,
            )

            composite.addForCanvasButtonLoading(
                id = type.retryId,
                isEnabled = true,
                text = weakResources.get()?.getString(R.string.try_again).orEmpty(),
                data = type,
            )

            return composite
        }

        private fun createTextComposerForSuccessState(
            type: FrequentOperationType,
        ) = TextComposerForQuantity(
            paddingEntity = paddingEntityForSuccessfulState,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            builder = TextBuilderForQuantity(appModel, weakResources, type.displayText),
        )

        private fun createComposerOfUnavailableImmediate(
            receiver: InstanceReceiver,
        ) = ComposerOfAlertBanner(
            collector = CollectorOfUnavailableImmediate(appModel, weakResources),
            receiver = receiver
        )

        private fun createComposerOfCheckableTransfer(
            receiver: InstanceReceiver,
        ) = ComposerOfCheckableTransfer(
            weakResources = weakResources,
            dividerPositions = dividerPositions,
            paddingEntity = paddingEntity,
            formatter = formatter,
            receiver = receiver,
            matcher = matcher,
        )

        private fun createCompositeForPaymentSection(
            visibilityPredicate: Predicate<FrequentOperationType>,
            receiver: InstanceReceiver,
            type: FrequentOperationType = FrequentOperationType.PAYMENT,
        ): CompositeForPaymentSection {

            val imageCollectorForDisabledOrErrorState = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)

            val textCollectorForDisabledState = createTextCollectorForDisabledOrErrorState(
                titleRes = R.string.my_list_disabled_payment_list_title,
                descriptionRes = R.string.my_list_disabled_payment_list_description,
            )

            val textCollectorForErrorState = createTextCollectorForDisabledOrErrorState(
                titleRes = R.string.my_list_error_payment_list_title,
                descriptionRes = R.string.my_list_error_payment_list_description,
            )

            val textCollectorForEmptyState = TextCollectorForPaymentEmptyState(horizontalPaddingEntity, weakResources)

            val composite = CompositeForPaymentSection(
                imagePaddingEntityForDisabledOrErrorState = imagePaddingEntityForSectionErrorState,
                imageComposerForDisabledOrErrorState = ComposerOfOneColumnImage(imageCollectorForDisabledOrErrorState, receiver),
                textComposerForDisabledState = ComposerOfOneColumnText(textCollectorForDisabledState),
                textComposerForErrorState = ComposerOfOneColumnText(textCollectorForErrorState),
                buttonComposerForErrorState = ComposerOfCanvasButtonLoading(buttonPaddingEntityForErrorState, receiver),
                textComposerForEmptyState = ComposerOfOneColumnText(textCollectorForEmptyState),
                textButtonComposerForEmptyState = createTextButtonComposerForEmptyState(receiver),
                labelButtonComposerForSuccessState = createLabelButtonComposerForSuccessState(receiver, type),
                composerOfCheckablePayment = createComposerOfCheckablePayment(receiver),
                composerOfSuccessfulPayment = createComposerOfSuccessfulPayment(receiver),
                visibilityPredicate = visibilityPredicate,
                frequentOperationType = type,
            )

            composite.addForCanvasButtonLoading(
                id = type.retryId,
                isEnabled = true,
                text = weakResources.get()?.getString(R.string.try_again).orEmpty(),
                data = type,
            )

            return composite
        }

        private fun createTextButtonComposerForEmptyState(
            receiver: InstanceReceiver,
        ) = TextButtonComposerForEmptyState(
            paddingEntity = horizontalPaddingEntity,
            weakResources = weakResources,
            receiver = receiver,
            templateForAddingRecentPayments = templateForAddingRecentPayments,
        )

        private fun createLabelButtonComposerForSuccessState(
            receiver: InstanceReceiver,
            type: FrequentOperationType,
        ) = LabelButtonComposerForSuccessState(
            paddingEntity = horizontalPaddingEntity,
            weakResources = weakResources,
            receiver = receiver,
            templateForAddingRecentPayments = templateForAddingRecentPayments,
            builder = TextBuilderForQuantity(appModel, weakResources, type.displayText),
        )

        private fun createComposerOfCheckablePayment(
            receiver: InstanceReceiver,
        ) = ComposerOfCheckablePayment(
            weakResources = weakResources,
            dividerPositions = dividerPositions,
            paddingEntity = paddingEntity,
            formatter = formatter,
            receiver = receiver,
            matcher = matcher,
        )

        private fun createComposerOfSuccessfulPayment(
            receiver: InstanceReceiver,
        ) = ComposerOfSuccessfulPayment(
            dividerPositions = dividerPositions,
            paddingEntity = paddingEntity,
            paddingOfLeftImage = imagePaddingEntityForSuccessfulPayment,
            receiver = receiver,
            matcher = matcher,
            weakResources = weakResources,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}