package pe.com.scotiabank.blpm.android.client.base.swipe

import androidx.annotation.ColorRes
import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.ComposerOfSwipe
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.SwipeController
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.SwipeState
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.UiEntityOfSwipe
import java.util.concurrent.ConcurrentHashMap

class SwipeComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfSwipe: ComposerOfSwipe,
    private val visibilitySupplier: Supplier<Boolean>,
) : DispatcherProvider by dispatcherProvider,
    SwipeController by composerOfSwipe
{

    private val compoundsByKey: MutableMap<Int, List<UiCompoundOfSingle<UiEntityOfSwipe>>?> = ConcurrentHashMap()
    val compounds: List<UiCompoundOfSingle<UiEntityOfSwipe>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    val currentState: SwipeState
        get() = composerOfSwipe.currentState

    fun setSwipe(
        state: SwipeState,
        @ColorRes backgroundColorRes: Int = R.color.canvascore_background,
        @ColorRes colorSchemaRes: List<Int> = listOf(R.color.canvascore_brand_red),
    ): SwipeComposite = apply {

        composerOfSwipe.editSwipe(state, backgroundColorRes, colorSchemaRes)
    }

    fun setSwipeState(state: SwipeState): SwipeComposite = apply {
        composerOfSwipe.editSwipeState(state)
    }

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompoundOfSingle<UiEntityOfSwipe>> {
        val swipeCompound = composerOfSwipe.composeUiData(
            visibilitySupplier = visibilitySupplier,
        )
        return listOf(swipeCompound)
    }

    class Factory(private val dispatcherProvider: DispatcherProvider) {

        fun create(
            receiver: InstanceReceiver,
            visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
        ) = SwipeComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfSwipe = ComposerOfSwipe(receiver),
            visibilitySupplier = visibilitySupplier,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
