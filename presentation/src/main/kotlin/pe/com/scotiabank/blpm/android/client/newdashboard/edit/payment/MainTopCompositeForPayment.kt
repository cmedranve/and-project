package pe.com.scotiabank.blpm.android.client.newdashboard.edit.payment

import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.IdentifiableEditText
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.CollectorOfEditableName
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.ComposerOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.util.concurrent.ConcurrentHashMap

class MainTopCompositeForPayment private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfEditableName: ComposerOfEditText<IdentifiableEditText>,
): DispatcherProvider by dispatcherProvider {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    val editableNameEntity: UiEntityOfEditText<IdentifiableEditText>? by composerOfEditableName::entity

    private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = R.dimen.canvascore_margin_16,
            right = R.dimen.canvascore_margin_16,
        )
    }

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val editableNameCompound = composerOfEditableName.composeUiData(
            paddingEntity = horizontalPaddingEntity,
        )

        return listOf(
            editableNameCompound,
        )
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val collectorOfEditableName: CollectorOfEditableName,
    ) {

        fun create(receiver: InstanceReceiver) = MainTopCompositeForPayment(
            dispatcherProvider = dispatcherProvider,
            composerOfEditableName = ComposerOfEditText(collectorOfEditableName, receiver),
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}