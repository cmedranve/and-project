package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import android.content.Context
import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.IdRegistry
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.debitcard.detail.screen.AtmCardService
import pe.com.scotiabank.blpm.android.client.debitcard.detail.screen.ComposerOfAtmCard
import pe.com.scotiabank.blpm.android.client.debitcard.detail.screen.ConverterOfAtmCard
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.ComposerOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.loading.ComposerOfLoading
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class CardDataComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val composerOfLoading: ComposerOfLoading,
    private val composerOfAlertBanner: ComposerOfErrorAlertBanner,
    private val composerOfAtmCard: ComposerOfAtmCard,
    private val composerOfTimerBuddyTip: ComposerOfTimerBuddyTip,
    private val composerOfShowDataCanvasButton: ComposerOfCanvasButton,
    private val composerOfRetryCanvasButton: ComposerOfCanvasButton,
    private val idRegistry: IdRegistry,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    UiStateHolder by uiStateHolder,
    AlertBannerService by composerOfAlertBanner,
    AtmCardService by composerOfAtmCard,
    TimerBuddyTipService by composerOfTimerBuddyTip
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    private val isAtmCardVisible: Boolean
        get() = UiState.SUCCESS == currentState || UiState.ERROR == currentState

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {
        val loadingCompound = composerOfLoading.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible),
        )

        val alertBannerCompound = composerOfAlertBanner.composeUiData(
            visibilitySupplier = Supplier(::isAtmCardVisible),
        )

        val atmCardCompound = composerOfAtmCard.composeUiData(
            visibilitySupplier = Supplier(::isAtmCardVisible)
        )

        val buddyTipTimerCompound = composerOfTimerBuddyTip.composeUiData(
            visibilitySupplier = Supplier(::isAtmCardVisible),
        )

        val showDataButtonCompound = composerOfShowDataCanvasButton.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        val retryButton = composerOfRetryCanvasButton.composeUiData(
            visibilitySupplier = Supplier(::isErrorVisible),
        )

        return listOf(
            loadingCompound,
            alertBannerCompound,
            atmCardCompound,
            buddyTipTimerCompound,
            showDataButtonCompound,
            retryButton
        )
    }

    fun setupShowDataButton(isDecrypted: Boolean) {
        composerOfShowDataCanvasButton.editCanvasButtonEnabling(idRegistry.showDataButtonId, !isDecrypted)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val appModel: AppModel,
        private val weakAppContext: WeakReference<Context?>,
        private val weakResources: WeakReference<Resources?>,
        private val credentialDataMapper: CredentialDataMapper,
        private val uiStateHolder: UiStateHolder,
        private val idRegistry: IdRegistry,
    ) {

        private val emptyPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding()
        }

        private val paddingEntityForLoading: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_width_190,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_width_190,
            )
        }

        private val paddingEntityForAlertBanner: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            )
        }

        private val paddingEntityForAtmCard: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
            )
        }

        private val paddingEntityForButton: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_30,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_36,
            )
        }

        fun create(receiver: InstanceReceiver): CardDataComposite = CardDataComposite(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            composerOfLoading = createComposerOfLoading(),
            composerOfAlertBanner = createComposerOfErrorAlertBanner(receiver),
            composerOfAtmCard = createComposerOfAtmCard(receiver),
            composerOfTimerBuddyTip = createComposerOfTimerBuddyTip(receiver),
            composerOfShowDataCanvasButton = createComposerOfShowDataCanvasButton(receiver),
            composerOfRetryCanvasButton = createComposerOfRetryCanvasButton(receiver),
            idRegistry = idRegistry,
        )

        private fun createComposerOfLoading(): ComposerOfLoading {
            val composerOfLoading = ComposerOfLoading(paddingEntityForLoading)
            composerOfLoading.add(id = randomLong())
            return composerOfLoading
        }

        private fun createComposerOfErrorAlertBanner(
            receiver: InstanceReceiver,
        ): ComposerOfErrorAlertBanner {
            val converter = ConverterOfErrorAlertBanner(
                typefaceProvider = appModel,
                weakResources = weakResources,
                receiver = receiver,
                paddingEntity = paddingEntityForAlertBanner
            )
            return ComposerOfErrorAlertBanner(converter)
        }

        private fun createComposerOfAtmCard(receiver: InstanceReceiver): ComposerOfAtmCard {
            val converter = ConverterOfAtmCard(
                weakResources = weakResources,
                mapper = credentialDataMapper,
                paddingEntity = paddingEntityForAtmCard,
                copyButtonId = idRegistry.copyButtonId,
                receiver = receiver,
            )
            return ComposerOfAtmCard(converter)
        }

        private fun createComposerOfTimerBuddyTip(
            receiver: InstanceReceiver,
        ): ComposerOfTimerBuddyTip {
            val converter = ConverterOfTimerBuddyTip(
                typefaceProvider = appModel,
                weakAppContext = weakAppContext,
                receiver = receiver,
                idRegistry = idRegistry,
                paddingEntity = emptyPaddingEntity,
            )
            return ComposerOfTimerBuddyTip(converter = converter)
        }

        private fun createComposerOfShowDataCanvasButton(receiver: InstanceReceiver): ComposerOfCanvasButton {
            val composerOfCanvasButton = ComposerOfCanvasButton(paddingEntityForButton, receiver)
            composerOfCanvasButton.add(
                id = idRegistry.showDataButtonId,
                isEnabled = false,
                text = weakResources.get()?.getString(R.string.show_data).orEmpty(),
            )
            return composerOfCanvasButton
        }

        private fun createComposerOfRetryCanvasButton(receiver: InstanceReceiver): ComposerOfCanvasButton {
            val composerOfCanvasButton = ComposerOfCanvasButton(paddingEntityForButton, receiver)
            composerOfCanvasButton.add(
                id = idRegistry.retryShowDataButtonId,
                isEnabled = true,
                text = weakResources.get()?.getString(R.string.retry).orEmpty(),
            )
            return composerOfCanvasButton
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0

    }
}
