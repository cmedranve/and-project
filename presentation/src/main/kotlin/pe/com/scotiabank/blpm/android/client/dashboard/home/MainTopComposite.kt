package pe.com.scotiabank.blpm.android.client.dashboard.home

import android.content.res.Resources
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val composerOfGateProduct: ComposerOfGateProduct,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    UiStateHolder by uiStateHolder
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()

    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    val composerOfProduct: ComposerOfGateProduct
        get() = composerOfGateProduct

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val productItemCompound = composerOfGateProduct.composeUiData()

        return listOf(
            productItemCompound
        )
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val uiStateHolder: UiStateHolder,
        private val appModel: AppModel,
        private val weakResources: WeakReference<Resources?>,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = R.dimen.canvascore_margin_16,
                right = R.dimen.canvascore_margin_16,
            )
        }

        fun create(receiver: InstanceReceiver): MainTopComposite = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfGateProduct = createComposerOfItemProduct(receiver),
            uiStateHolder = uiStateHolder,
        )

        private fun createComposerOfItemProduct(
            receiver: InstanceReceiver,
        ): ComposerOfGateProduct = ComposerOfGateProduct(
            appModel = appModel,
            paddingEntity = horizontalPaddingEntity,
            receiver = receiver,
            weakResources = weakResources,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}