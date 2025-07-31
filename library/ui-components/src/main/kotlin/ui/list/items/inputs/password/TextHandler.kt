package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import androidx.core.util.Function
import androidx.core.util.Supplier
import com.scotiabank.canvascore.inputs.PasswordInputView
import com.scotiabank.enhancements.handling.InstanceReceiver

internal class TextHandler<D: Any>(
    private val entity: UiEntityOfPassword<D>,
    private val textSupplying: Supplier<CharSequence>,
) : PasswordInputView.EventCallback {

    private val receiver: InstanceReceiver?
        get() = entity.receiver

    override fun onEvent() {
        val text: CharSequence = textSupplying.get()
        entity.text = text
        identifyStatuses(text)
        receiver?.receive(entity)
    }

    private fun identifyStatuses(text: CharSequence) {
        val requirementEntities: List<UiEntityOfRequirement> = entity.requirementEntities
        requirementEntities.forEach { requirementEntity -> identifyStatus(text, requirementEntity) }
    }

    private fun identifyStatus(text: CharSequence, requirementEntity: UiEntityOfRequirement) {
        val identifier: Function<CharSequence, RequirementStatus> = requirementEntity.statusIdentifier
        requirementEntity.mutableStatus = identifier.apply(text)
    }
}
