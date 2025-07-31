package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.content.Context
import android.content.res.Resources
import android.text.InputFilter
import androidx.annotation.StringRes
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.errorstate.CompositeOfErrorState
import pe.com.scotiabank.blpm.android.client.base.number.DoubleParser
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.onecolumn.TextCollectorForDisabledOrErrorState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.DecimalDigitsInputFilter
import pe.com.scotiabank.blpm.android.ui.list.items.loading.ComposerOfLoading
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    val compositeOfErrorState: CompositeOfErrorState,
    private val composerOfCardImage: ComposerOfCardImage,
    private val composerOfOneColumnText: ComposerOfOneColumnText,
    private val composerOfCardSetting: ComposerOfCardSetting,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    UiStateHolder by uiStateHolder,
    CardService by composerOfCardSetting,
    CardImageService by composerOfCardImage
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val mutableCompounds: MutableList<UiCompound<*>> = mutableListOf()

        val compoundsOfErrorState: List<UiCompound<*>> = compositeOfErrorState.compose()
        mutableCompounds.addAll(compoundsOfErrorState)

        val imageCompound = composerOfCardImage.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        mutableCompounds.add(imageCompound)

        val textCompound = composerOfOneColumnText.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        mutableCompounds.add(textCompound)

        val cardCompound = composerOfCardSetting.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        mutableCompounds.add(cardCompound)

        return mutableCompounds
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val defaultLocale: Locale,
        private val appModel: AppModel,
        private val weakResources: WeakReference<Resources?>,
        private val weakAppContext: WeakReference<Context?>,
        private val card: AtmCardInfo,
        private val uiStateHolder: UiStateHolder,
        private val idRegistry: IdRegistry,
        private val doubleParser: DoubleParser,
        private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            )
        }

        private val textPaddingEntityForErrorState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val imagePaddingEntityForErrorState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_spacing_20,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val paddingEntityForErrorLoading: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_36,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_36,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val filters: Array<InputFilter> by lazy {
            val currencyFilter = DecimalDigitsInputFilter(
                digitsBeforeDecimalSeparator = 5,
                digitsAfterDecimalSeparator = 2,
                locale = defaultLocale,
            )
            arrayOf(currencyFilter)
        }

        fun create(
            receiver: InstanceReceiver,
        ): MainTopComposite = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            compositeOfErrorState = createCompositeForErrorState(receiver),
            composerOfCardImage = createComposerForCardImage(),
            composerOfOneColumnText = createComposerOfOneColumnText(),
            composerOfCardSetting = createComposerOfCard(receiver),
        )

        private fun createCompositeForErrorState(receiver: InstanceReceiver): CompositeOfErrorState {

            val imageCollector = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)

            val textCollector = createTextCollectorForErrorState(
                titleRes = R.string.contact_payment_error_title,
                descriptionRes = R.string.contact_payment_error_description,
            )

            val loadingComposer = ComposerOfLoading(paddingEntityForErrorLoading)
            loadingComposer.add(id = randomLong())

            return CompositeOfErrorState(
                imagePaddingEntity = imagePaddingEntityForErrorState,
                imageComposer = ComposerOfOneColumnImage(imageCollector, receiver),
                textComposer = ComposerOfOneColumnText(textCollector),
                loadingComposer = loadingComposer,
                visibilitySupplier = Supplier(uiStateHolder::isErrorVisible),
            )
        }

        private fun createTextCollectorForErrorState(
            @StringRes titleRes: Int,
            @StringRes descriptionRes: Int,
        ) = TextCollectorForDisabledOrErrorState(
            paddingEntity = textPaddingEntityForErrorState,
            weakResources = weakResources,
            titleRes = titleRes,
            descriptionRes = descriptionRes,
        )

        private fun createComposerForCardImage(): ComposerOfCardImage {
            val converter = ConverterOfCardImage(
                brand = card.atmCard.brand,
                idRegistry = idRegistry,
            )
            return ComposerOfCardImage(converter = converter)
        }

        private fun createComposerOfOneColumnText(): ComposerOfOneColumnText {
            val collector = CollectorOfOneColumnText(
                card = card,
                factory = factoryOfOneColumnTextEntity,
                horizontalPaddingEntity = horizontalPaddingEntity,
            )
            return ComposerOfOneColumnText(collector)
        }

        private fun createComposerOfCard(
            receiver: InstanceReceiver,
        ): ComposerOfCardSetting {
            val collector = ConverterOfCardSetting(
                typefaceProvider = appModel,
                atmCardType = card.atmCard.type,
                horizontalPaddingEntity = horizontalPaddingEntity,
                filters = filters,
                receiver = receiver,
                weakAppContext = weakAppContext,
                factory = factoryOfOneColumnTextEntity,
                doubleParser = doubleParser,
                textConverterForHowItWorks = createTextConverterForHowItWorks(receiver)
            )
            return ComposerOfCardSetting(converter = collector)
        }

        private fun createTextConverterForHowItWorks(
            receiver: InstanceReceiver,
        ): TextConverterForHowItWorks = TextConverterForHowItWorks(
            typefaceProvider = appModel,
            weakAppContext = weakAppContext,
            factory = factoryOfOneColumnTextEntity,
            receiver = receiver,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}