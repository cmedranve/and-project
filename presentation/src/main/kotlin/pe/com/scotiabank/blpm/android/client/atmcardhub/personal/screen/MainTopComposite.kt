package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen.skeleton.CollectorOfSkeletonContent
import pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen.skeleton.CollectorOfSkeletonTitle
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardButton
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.CollectorOfTitle
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.FactoryOfCardEntity
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.ComposerOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.CollectorOfQuickActionCard
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.ComposerOfQuickActionCard
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfFullError: ComposerOfOneColumnText,
    private val composerOfOneColumnImage: ComposerOfOneColumnImage,
    private val compositeForDebitCardSection: CompositeForDebitCardSection,
    private val compositeForCreditCardSection: CompositeForCreditCardSection,
    private val composerOfGooglePayQuickActionCard: ComposerOfQuickActionCard<*>,
    override var currentState: UiState = UiState.BLANK,
) : Composite, DispatcherProvider by dispatcherProvider, UiStateHolder {

    val compositeForDebitCard: CompositeForDebitCardSection = compositeForDebitCardSection
    val compositeForCreditCard: CompositeForCreditCardSection = compositeForCreditCardSection

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> =
        ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    val numDebitCards: Int
        get() = compositeForDebitCard.composerOfHubDebitCard.cardEntities.size

    val numCreditCards: Int
        get() = compositeForCreditCard.composerOfHubCreditCard.cardEntities.size

    val isFullScreenError: Boolean
        get() = compositeForCreditCard.currentState == UiState.ERROR
                && compositeForDebitCard.currentState == UiState.ERROR

    val isFullScreenEmpty: Boolean
        get() = compositeForCreditCard.currentState == UiState.EMPTY
                && compositeForDebitCard.currentState == UiState.EMPTY

    val containExtraLine: Boolean
        get() = compositeForCreditCard.composerOfHubCreditCard
                .fetchAll()
                .any { product -> Constant.EL.equals(product.subProductType, true) }

    val existsActiveDebitCards: Boolean
        get() = compositeForDebitCard.composerOfHubDebitCard.cardEntities.isNotEmpty()

    val existsActiveCreditCards: Boolean
        get() = compositeForCreditCard.composerOfHubCreditCard.fetchAll().any { card ->
            card.isInactive.not() && Constant.EL.equals(card.subProductType, true).not()
        }

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private val paddingEntityOfErrorImage: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_spacing_17,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
        )
    }

    private val paddingEntityOfGooglePayCard: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            left = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            right = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
        )
    }

    private fun composeItself(): List<UiCompound<*>> {

        val compoundOfImage = composerOfOneColumnImage.composeUiData(
            paddingEntity = paddingEntityOfErrorImage,
            visibilitySupplier = Supplier(::isErrorVisible)
        )

        val compoundForActionButton = composerOfFullError.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible)
        )

        val mutableCompounds: MutableList<UiCompound<*>> = mutableListOf(
            compoundOfImage,
            compoundForActionButton,
        )

        val creditCardSectionCompound = compositeForCreditCardSection.compose()
        mutableCompounds.addAll(creditCardSectionCompound)

        val debitCardSectionCompound = compositeForDebitCardSection.compose()
        mutableCompounds.addAll(debitCardSectionCompound)

        val compoundForGooglePayCard = composerOfGooglePayQuickActionCard.composeUiData(
            paddingEntity = paddingEntityOfGooglePayCard,
            visibilitySupplier = Supplier(::isSuccessVisible)
        )
        mutableCompounds.add(compoundForGooglePayCard)

        return mutableCompounds
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
        private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity,
        private val navigationTemplate: NavigationTemplate,
        private val collectorOfGooglePayQuickActionCard: CollectorOfQuickActionCard<*>,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            )
        }

        private val paddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        fun create(receiver: InstanceReceiver) = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfFullError = createComposerOfFullError(),
            composerOfOneColumnImage = createComposerOfOneColumnImage(receiver),
            compositeForDebitCardSection = createCompositeForDebitCardSection(receiver),
            compositeForCreditCardSection = createCompositeForCreditCardSection(receiver),
            composerOfGooglePayQuickActionCard = ComposerOfQuickActionCard(collectorOfGooglePayQuickActionCard, receiver),
        )

        private fun createComposerOfFullError(): ComposerOfOneColumnText {
            val collector = CollectorOfFullErrorText(
                weakResources = weakResources,
                factory = factoryOfOneColumnTextEntity,
            )
            return ComposerOfOneColumnText(collector)
        }

        private fun createComposerOfOneColumnImage(
            receiver: InstanceReceiver,
        ) = ComposerOfOneColumnImage(
            collector = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list),
            receiver = receiver,
        )

        private fun createCompositeForDebitCardSection(
            receiver: InstanceReceiver,
        ): CompositeForDebitCardSection = CompositeForDebitCardSection(
            composerOfTitle = createComposerOfDebitTitle(),
            skeletonComposerForBodyLoading = createComposerOfSkeletonForBodyLoading(),
            composerOfErrorCard = createComposerOfErrorDebitCard(receiver),
            composerOfHubPendingDebitCard = createComposerOfHubPendingDebitCard(receiver),
            composerOfHubDebitCard = createComposerOfHubDebitCard(receiver),
        )

        private fun createComposerOfDebitTitle(): ComposerOfOneColumnText {
            val title: String = weakResources.get()?.getString(R.string.cards_settings_debit).orEmpty()
            val collector = CollectorOfTitle(factoryOfOneColumnTextEntity, paddingEntity, title)
            return ComposerOfOneColumnText(collector)
        }

        private fun createComposerOfSkeletonForBodyLoading(): ComposerOfSkeleton {
            val collector = CollectorOfSkeletonContent(paddingEntity)
            return ComposerOfSkeleton(collector)
        }

        private fun createComposerOfErrorDebitCard(
            receiver: InstanceReceiver,
        ): ComposerOfCard {

            val collector = CollectorOfErrorCardSection(
                weakResources = weakResources,
                reloadType = ReloadType.GO_RETRY_DEBIT_CARDS,
                receiver = receiver,
                factory = factoryOfOneColumnTextEntity,
                paddingEntity = paddingEntity,
            )

            return ComposerOfCard(collector)
        }

        private fun createComposerOfHubPendingDebitCard(
            receiver: InstanceReceiver,
        ): ComposerOfHubPendingDebitCard {
            val converter = ConverterOfHubPendingDebitCard(
                weakResources = weakResources,
                factoryOfOneColumnTextEntity = factoryOfOneColumnTextEntity,
                converterOfCardInfo = ConverterOfCardInfo(factoryOfOneColumnTextEntity, receiver),
                converterOfCardButton = ConverterOfCardButton(weakResources, receiver),
                factoryOfCardEntity = FactoryOfCardEntity(horizontalPaddingEntity),
            )
            return ComposerOfHubPendingDebitCard(converter = converter)
        }

        private fun createComposerOfHubDebitCard(
            receiver: InstanceReceiver,
        ): ComposerOfHubDebitCard {
            val converter = ConverterOfHubDebitCard(
                weakResources = weakResources,
                factoryOfOneColumnTextEntity = factoryOfOneColumnTextEntity,
                converterOfCardInfo = ConverterOfCardInfo(factoryOfOneColumnTextEntity, receiver),
                converterOfCardButton = ConverterOfCardButton(weakResources, receiver),
                factoryOfCardEntity = FactoryOfCardEntity(horizontalPaddingEntity),
            )
            return ComposerOfHubDebitCard(converter = converter)
        }

        private fun createCompositeForCreditCardSection(
            receiver: InstanceReceiver,
        ): CompositeForCreditCardSection = CompositeForCreditCardSection(
            composerOfTitle = createComposerOfCreditTitle(),
            composerOfSkeletonTitle = createComposerOfSkeletonTitle(),
            skeletonComposerForBodyLoading = createComposerOfSkeletonForBodyLoading(),
            composerOfHubCreditCard = createComposerOfHubCreditCard(receiver),
            composerOfErrorCard = createComposerOfErrorCreditCard(receiver)
        )

        private fun createComposerOfCreditTitle(): ComposerOfOneColumnText {
            val title: String = weakResources.get()?.getString(R.string.cards_settings_credit).orEmpty()
            val collector = CollectorOfTitle(factoryOfOneColumnTextEntity, paddingEntity, title)
            return ComposerOfOneColumnText(collector)
        }

        private fun createComposerOfSkeletonTitle(): ComposerOfSkeleton {
            val collector = CollectorOfSkeletonTitle(paddingEntity)
            return ComposerOfSkeleton(collector)
        }

        private fun createComposerOfHubCreditCard(
            receiver: InstanceReceiver,
        ): ComposerOfHubCreditCard {
            val converter = ConverterOfHubCreditCard(
                weakResources = weakResources,
                factoryOfOneColumnTextEntity = factoryOfOneColumnTextEntity,
                converterOfCardInfo = ConverterOfCardInfo(factoryOfOneColumnTextEntity, receiver),
                converterOfCardButton = ConverterOfCardButton(weakResources, receiver),
                optionTemplateOfDataButton = getOptionTemplateOfDataButton(),
                factoryOfCardEntity = FactoryOfCardEntity(horizontalPaddingEntity),
            )
            return ComposerOfHubCreditCard(converter = converter)
        }

        private fun createComposerOfErrorCreditCard(
            receiver: InstanceReceiver,
        ): ComposerOfCard {

            val collector = CollectorOfErrorCardSection(
                weakResources = weakResources,
                reloadType = ReloadType.GO_RETRY_CREDIT_CARDS,
                receiver = receiver,
                factory = factoryOfOneColumnTextEntity,
                paddingEntity = paddingEntity,
            )

            return ComposerOfCard(collector)
        }

        private fun getOptionTemplateOfDataButton(): OptionTemplate = TemplatesUtil.getOperation(
            navigation = navigationTemplate,
            featureName = TemplatesUtil.CARD_CREDENTIALS,
            optionName = TemplatesUtil.SHOW_CREDIT_CARD_CREDENTIALS_WITH_DCVV2,
        )
    }

    companion object {
        private val SINGLE_KEY: Int
            get() = 0
    }
}
