package pe.com.scotiabank.blpm.android.client.host.session.postloading

import androidx.core.util.Supplier
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.loading.ComposerOfLoading
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val loadingComposer: ComposerOfLoading,
) : Composite, DispatcherProvider by dispatcherProvider, UiStateHolder by uiStateHolder {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()

    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val loadingCompound = loadingComposer.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible),
        )

        return listOf(loadingCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val uiStateHolder: UiStateHolder,
        private val idRegistry: IdRegistry,
    ) {

        private val paddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_42,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_42,
            )
        }

        fun create() = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            loadingComposer = createLoadingComposer(),
        )

        private fun createLoadingComposer(): ComposerOfLoading {
            val composer = ComposerOfLoading(paddingEntity)
            composer.add(id = idRegistry.loadingId)
            return composer
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}