package pe.com.scotiabank.blpm.android.ui.list.items.swipe

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle

class ComposerOfSwipe(private val receiver: InstanceReceiver) : SwipeController {

    private val entity: UiEntityOfSwipe by lazy {
        UiEntityOfSwipe(
            state = SwipeState.DISABLED,
            receiver = receiver,
        )
    }

    val currentState: SwipeState
        get() = entity.state

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompoundOfSingle<UiEntityOfSwipe> = UiCompoundOfSingle(entity, visibilitySupplier)

    override fun editSwipe(
        state: SwipeState,
        backgroundColorRes: Int,
        colorSchemaRes: List<Int>,
    ) {
        entity.state = state
        entity.backgroundColorRes = backgroundColorRes
        entity.colorSchemaRes = entity.colorSchemaRes
    }

    override fun editSwipeState(state: SwipeState) {
        entity.state = state
    }
}
