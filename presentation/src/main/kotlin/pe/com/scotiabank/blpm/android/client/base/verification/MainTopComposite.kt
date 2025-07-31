package pe.com.scotiabank.blpm.android.client.base.verification

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.Channel
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.ChannelRegistry
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.ChannelService
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.ComposerOfNumberInput
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.ConverterOfNumberInput
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.NumberInputService
import pe.com.scotiabank.blpm.android.client.nosession.shared.otpgroup.ComposerOfOtpGroup
import pe.com.scotiabank.blpm.android.client.nosession.shared.otpgroup.ConverterOfOtpGroup
import pe.com.scotiabank.blpm.android.client.nosession.shared.verificationscreen.ComposerOfSubhead
import pe.com.scotiabank.blpm.android.client.nosession.shared.verificationscreen.ConverterOfSubhead
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val composerOfHeadline: ComposerOfHeadline,
    private val composerOfSubhead: ComposerOfSubhead,
    private val composerOfDigitalKey: ComposerOfNumberInput,
    private val composerOfOtpGroup: ComposerOfOtpGroup,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    UiStateHolder by uiStateHolder,
    ChannelService by composerOfOtpGroup,
    NumberInputService by composerOfDigitalKey
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override fun clearThenAddChannel(channel: Channel) {
        composerOfSubhead.clearThenAddChannel(channel)
        composerOfOtpGroup.clearThenAddChannel(channel)
    }

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val headlineCompound = composerOfHeadline.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        val subheadCompound = composerOfSubhead.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        val digitalKeyCompound = composerOfDigitalKey.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        val otpGroupCompound = composerOfOtpGroup.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        return listOf(
            headlineCompound,
            subheadCompound,
            digitalKeyCompound,
            otpGroupCompound,
        )
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
        private val uiStateHolder: UiStateHolder,
        private val channelRegistry: ChannelRegistry,
        private val idRegistry: IdRegistry,
        private val factory: FactoryOfOneColumnTextEntity,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = R.dimen.canvascore_margin_16,
                right = R.dimen.canvascore_margin_16,
            )
        }

        private val paddingEntityForHeadline: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = R.dimen.canvascore_margin_40,
                bottom = R.dimen.canvascore_margin_36,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val paddingEntityForSubhead: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        fun create(receiver: InstanceReceiver): MainTopComposite = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            composerOfHeadline = createComposerOfHeadline(),
            composerOfSubhead = createComposerOfSubhead(),
            composerOfDigitalKey = createComposerOfDigitalKey(receiver),
            composerOfOtpGroup = createComposerOfOtpGroup(receiver),
        )

        private fun createComposerOfHeadline(): ComposerOfHeadline {
            val collector = CollectorOfHeadline(
                weakResources = weakResources,
                paddingEntity = paddingEntityForHeadline,
                factory = factory,
            )
            return ComposerOfHeadline(collector)
        }

        private fun createComposerOfSubhead(): ComposerOfSubhead {
            val converter = ConverterOfSubhead(
                weakResources = weakResources,
                channelRegistry = channelRegistry,
                paddingEntity = paddingEntityForSubhead,
                factory = factory,
            )
            return ComposerOfSubhead(converter)
        }

        private fun createComposerOfDigitalKey(receiver: InstanceReceiver): ComposerOfNumberInput {
            val converter = ConverterOfNumberInput(
                weakResources = weakResources,
                paddingEntity = horizontalPaddingEntity,
                receiver = receiver,
            )
            return ComposerOfNumberInput(converter)
        }

        private fun createComposerOfOtpGroup(receiver: InstanceReceiver): ComposerOfOtpGroup {
            val converter = ConverterOfOtpGroup(
                weakResources = weakResources,
                channelRegistry = channelRegistry,
                idOfGroup = idRegistry.idOfGroup,
                idOfRetrying = idRegistry.idOfRetrying,
                idOfSendingBy = idRegistry.idOfSendingBy,
                paddingEntity = horizontalPaddingEntity,
                receiver = receiver,
            )
            return ComposerOfOtpGroup(converter)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
