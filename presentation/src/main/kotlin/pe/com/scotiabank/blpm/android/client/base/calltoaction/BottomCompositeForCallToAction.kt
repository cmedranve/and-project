package pe.com.scotiabank.blpm.android.client.base.calltoaction

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class BottomCompositeForCallToAction(
    dispatcherProvider: DispatcherProvider,
    private val visibilitySupplier: Supplier<Boolean>,
    private val composerOfCallToAction: ComposerOfCallToAction,
) : Composite, DispatcherProvider by dispatcherProvider {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()

    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val callToActionCompound = composerOfCallToAction.composeUiData(
            visibilitySupplier = visibilitySupplier,
        )

        return listOf(callToActionCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
        private val callToActions: List<CallToAction>,
    ) : Composite.Factory {

        private val paddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = R.dimen.canvascore_margin_12,
                bottom = R.dimen.canvascore_margin_12,
                left = R.dimen.canvascore_margin_16,
                right = R.dimen.canvascore_margin_16,
            )
        }

        override fun create(
            receiver: InstanceReceiver,
            visibilitySupplier: Supplier<Boolean>,
        ): BottomCompositeForCallToAction {

            val composerOfCallToAction: ComposerOfCallToAction = createComposerOfCallToAction(
                receiver = receiver,
            )
            callToActions.forEach(composerOfCallToAction::add)

            return BottomCompositeForCallToAction(
                dispatcherProvider = dispatcherProvider,
                visibilitySupplier = visibilitySupplier,
                composerOfCallToAction = composerOfCallToAction,
            )
        }

        private fun createComposerOfCallToAction(receiver: InstanceReceiver): ComposerOfCallToAction {
            return ComposerOfCallToAction(weakResources, paddingEntity, receiver)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
