package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen.CollectorOfFullErrorText
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardButton
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.CollectorOfTitle
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.FactoryOfCardEntity
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolderWithErrorType,
    val composerOfLoadingSkeleton: ComposerOfSkeleton,
    private val composerOfErrorText: ComposerOfOneColumnText,
    private val composerOfErrorImage: ComposerOfOneColumnImage,
    val compositeForCreditCardSection: CompositeForCardSection,
    val compositeForDebitCardSection: CompositeForCardSection,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    UiStateHolderWithErrorType by uiStateHolder
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private val paddingEntityOfErrorImage: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_spacing_17,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
        )
    }

    private fun composeItself(): List<UiCompound<*>> {

        val mutableCompounds: MutableList<UiCompound<*>> = mutableListOf()

        val loadingCompound = composerOfLoadingSkeleton.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible)
        )
        mutableCompounds.add(loadingCompound)

        val errorImageCompound = composerOfErrorImage.composeUiData(
            paddingEntity = paddingEntityOfErrorImage,
            visibilitySupplier = Supplier(::isEmptyOrErrorVisible)
        )
        mutableCompounds.add(errorImageCompound)

        val errorTextCompound = composerOfErrorText.composeUiData(
            visibilitySupplier = Supplier(::isEmptyOrErrorVisible)
        )
        mutableCompounds.add(errorTextCompound)

        val creditCardSectionCompound = compositeForCreditCardSection.compose()
        mutableCompounds.addAll(creditCardSectionCompound)

        val debitCardSectionCompound = compositeForDebitCardSection.compose()
        mutableCompounds.addAll(debitCardSectionCompound)

        return mutableCompounds
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val uiStateHolder: UiStateHolderWithErrorType,
        private val weakResources: WeakReference<Resources?>,
        private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity,
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
            uiStateHolder = uiStateHolder,
            composerOfLoadingSkeleton = createComposerOfSkeletonForLoading(),
            composerOfErrorImage = createComposerOfErrorImage(),
            composerOfErrorText = createComposerOfErrorText(),
            compositeForCreditCardSection = createCompositeForCreditCardSection(receiver),
            compositeForDebitCardSection = createCompositeForDebitCardSection(receiver),
        )

        private fun createComposerOfSkeletonForLoading(): ComposerOfSkeleton {
            val collector = SkeletonCollectorForLoading(paddingEntity)
            return ComposerOfSkeleton(collector)
        }

        private fun createComposerOfErrorImage(): ComposerOfOneColumnImage {
            val collector = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)
            return ComposerOfOneColumnImage(collector)
        }

        private fun createComposerOfErrorText(): ComposerOfOneColumnText {
            val collector = CollectorOfFullErrorText(
                weakResources = weakResources,
                factory = factoryOfOneColumnTextEntity,
            )
            return ComposerOfOneColumnText(collector)
        }

        private fun createCompositeForCreditCardSection(
            receiver: InstanceReceiver,
        ) = CompositeForCardSection(
            composerOfTitle = createComposerOfCreditTitle(),
            composerOfAnyCard = createComposerOfCard(receiver),
        )

        private fun createComposerOfCreditTitle(): ComposerOfOneColumnText {
            val title: String = weakResources.get()?.getString(R.string.cards_settings_credit).orEmpty()
            val collector: CollectorOfOneColumnText = CollectorOfTitle(
                factory = factoryOfOneColumnTextEntity,
                paddingEntity = paddingEntity,
                title = title,
            )
            return ComposerOfOneColumnText(collector)
        }

        private fun createComposerOfCard(receiver: InstanceReceiver): ComposerOfAnyCard {
            val converter = ConverterOfAnyCard(
                weakResources = weakResources,
                factoryOfOneColumnTextEntity = factoryOfOneColumnTextEntity,
                converterOfCardInfo = ConverterOfCardInfo(factoryOfOneColumnTextEntity, receiver),
                converterOfCardButton = ConverterOfCardButton(weakResources, receiver),
                factoryOfCardEntity = FactoryOfCardEntity(horizontalPaddingEntity),
            )
            return ComposerOfAnyCard(converter)
        }

        private fun createCompositeForDebitCardSection(
            receiver: InstanceReceiver,
        ) = CompositeForCardSection(
            composerOfTitle = createComposerOfDebitTitle(),
            composerOfAnyCard = createComposerOfCard(receiver),
        )

        private fun createComposerOfDebitTitle(): ComposerOfOneColumnText {
            val title: String = weakResources.get()?.getString(R.string.cards_settings_debit).orEmpty()
            val collector: CollectorOfOneColumnText = CollectorOfTitle(
                factory = factoryOfOneColumnTextEntity,
                paddingEntity = paddingEntity,
                title = title,
            )
            return ComposerOfOneColumnText(collector)
        }
    }

    companion object {
        private val SINGLE_KEY: Int
            get() = 0
    }
}
