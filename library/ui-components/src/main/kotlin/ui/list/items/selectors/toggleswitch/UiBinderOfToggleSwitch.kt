package pe.com.scotiabank.blpm.android.ui.list.items.selectors.toggleswitch

import android.widget.CompoundButton
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.databinding.ViewToggleSwitchItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.UiBinderOfCompoundButton
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfToggleSwitch {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfToggleSwitch<D>, ViewToggleSwitchItemBinding>,
    ) {
        val entity: UiEntityOfToggleSwitch<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfToggleSwitch<D>,
        binding: ViewToggleSwitchItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)

        binding.tSwitch.setOnCheckedChangeListener(null)

        UiBinderOfCompoundButton.bind(entity.isEnabled, entity.isChecked, binding.tSwitch)
        binding.tSwitch.text = entity.text

        bindCheckingChangeCallback(entity, binding.tSwitch)
    }

    @JvmStatic
    private fun <D: Any> bindCheckingChangeCallback(
        entity: UiEntityOfToggleSwitch<D>,
        view: CompoundButton
    ) {
        view.setOnCheckedChangeListener { _, isChecked -> handleCheckingChange(entity, isChecked) }
    }

    @JvmStatic
    private fun <D: Any> handleCheckingChange(entity: UiEntityOfToggleSwitch<D>, isChecked: Boolean) {
        entity.isChecked = isChecked
        val receiver: InstanceReceiver = entity.receiver ?: return
        receiver.receive(entity)
    }
}
