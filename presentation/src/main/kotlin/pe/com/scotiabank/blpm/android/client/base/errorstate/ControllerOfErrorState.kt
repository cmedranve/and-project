package pe.com.scotiabank.blpm.android.client.base.errorstate

import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.errorhandling.SuspendingReceiverOfError
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.UiEvent
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry

class ControllerOfErrorState<O: Any>(
    private val dataFunction: SuspendingFunction<Map<Long, Any?>, O>,
    private val receiverOfErrorStateEvents: SuspendingReceiverOfInstance,
    private val errorReceiverOfErrorStateEvents: SuspendingReceiverOfError,
    private val retryButtonId: Long,
    private val visitRegistry: VisitRegistry,
    private val availabilityRegistry: AvailabilityRegistry,
    private val errorTopComposite: CompositeOfErrorState,
    private val errorBottomComposite: BottomComposite,
) {

    @JvmOverloads
    suspend fun tryGetting(inputData: Map<Long, Any?> = emptyMap()) = try {
        val outputData: O = dataFunction.apply(inputData)
        errorTopComposite.currentErrorSubState = UiErrorSubState.IDLE
        errorBottomComposite.editCanvasButtonEnabling(id = retryButtonId, isEnabled = true)
        availabilityRegistry.setAvailability(retryButtonId, true)
        receiverOfErrorStateEvents.receive(outputData)
    } catch (throwable: Throwable) {
        availabilityRegistry.setAvailability(retryButtonId, true)
        errorReceiverOfErrorStateEvents.receive(throwable)
    }

    suspend fun showErrorState() {
        errorTopComposite.currentErrorSubState = UiErrorSubState.IDLE
        errorBottomComposite.editCanvasButtonEnabling(
            id = retryButtonId,
            isEnabled = visitRegistry.isVisitAllowed(retryButtonId),
        )
        receiverOfErrorStateEvents.receive(UiEvent.NOTIFY_UI)
    }

    @JvmOverloads
    suspend fun retryGetting(inputData: Map<Long, Any?> = emptyMap()) {
        val isUnavailable: Boolean = availabilityRegistry.isAvailable(retryButtonId).not()
        if (isUnavailable) return

        availabilityRegistry.setAvailability(retryButtonId, false)

        errorTopComposite.currentErrorSubState = UiErrorSubState.LOADING
        errorBottomComposite.editCanvasButtonEnabling(id = retryButtonId, isEnabled = true)
        receiverOfErrorStateEvents.receive(UiEvent.NOTIFY_UI)
        tryGetting(inputData)
    }
}
