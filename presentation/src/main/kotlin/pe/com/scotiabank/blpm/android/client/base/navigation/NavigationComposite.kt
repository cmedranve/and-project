package pe.com.scotiabank.blpm.android.client.base.navigation

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.ComposerOfNavigation
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.NavigationController
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.UiEntityOfNavigation
import java.util.concurrent.ConcurrentHashMap

class NavigationComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfNavigation: ComposerOfNavigation,
    private val visibilitySupplier: Supplier<Boolean>,
) : CompositeOfSingle<UiEntityOfNavigation>,
    DispatcherProvider by dispatcherProvider,
    NavigationController by composerOfNavigation
{

    private val compoundsByKey: MutableMap<Int, List<UiCompoundOfSingle<UiEntityOfNavigation>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompoundOfSingle<UiEntityOfNavigation>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompoundOfSingle<UiEntityOfNavigation>> {
        val navigationCompound = composerOfNavigation.composeUiData(
            visibilitySupplier = visibilitySupplier,
        )
        return listOf(navigationCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val coordinatorId: Long,
    ) {

        fun create(
            receiver: InstanceReceiver,
            selectedItemId: Long = NO_ID,
            visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
        ) : NavigationComposite = NavigationComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfNavigation = ComposerOfNavigation(coordinatorId, selectedItemId, receiver),
            visibilitySupplier = visibilitySupplier,
        )
    }

    companion object {

        private val NO_ID: Long
            get() = -1
        private val SINGLE_KEY: Int
            get() = 0
    }
}
