package pe.com.scotiabank.blpm.android.client.host

import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val composerOfSkeleton: ComposerOfSkeleton,
): Composite, DispatcherProvider by dispatcherProvider, UiStateHolder by uiStateHolder {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) {
            composeItself()
        }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val skeletonCompoundForLoadingState = composerOfSkeleton.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible),
        )

        return listOf(
            skeletonCompoundForLoadingState,
        )
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val uiStateHolder: UiStateHolder,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = R.dimen.canvascore_margin_16,
                right = R.dimen.canvascore_margin_16,
            )
        }

        fun create() = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            composerOfSkeleton = createComposerOfSkeleton(),
        )

        private fun createComposerOfSkeleton() = ComposerOfSkeleton(
            collector = SkeletonCollectorForLoading(horizontalPaddingEntity),
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
