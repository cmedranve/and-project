package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.checkbox

import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.common.util.BiConsumer
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCheckBoxItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfSideLinearLayout
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiBinderOfRecyclerForOtherType
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UpdaterOfConstraintLayoutParams
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.UiBinderOfCompoundButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

object UiBinderOfCheckBox {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxItemBinding>
    ) {
        val entity: UiEntityOfCheckableButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxItemBinding>,
        entity: UiEntityOfCheckableButton<D>,
        binding: ViewCheckBoxItemBinding
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.llCcb)

        val updaterForSideItems = UpdaterOfConstraintLayoutParams(binding.rvSideItems)
        val updaterForBottomItems = UpdaterOfConstraintLayoutParams(binding.rvBottomItems)

        UiBinderOfWidthParam.bind(
            child = binding.root,
            expectedFlexGrow = entity.expectedFlexGrow,
            layoutParamsUpdatersForNested = listOf(updaterForSideItems, updaterForBottomItems),
        )

        UiBinderOfSideLinearLayout.bind(
            paddingEntity = entity.paddingEntityOfCheckableIcon,
            bias = entity.verticalBiasOfCheckableIcon,
            llSide = binding.llCcb,
        )

        val checkBox: CheckBox = binding.ccb.getCheckBox() ?: return

        checkBox.setOnCheckedChangeListener(null)
        binding.cLayout.setOnClickListener(null)

        UiBinderOfCompoundButton.bind(entity.isEnabled, entity.isChecked, checkBox)
        bindIfDifferent(entity.isEnabled, binding.cLayout::isEnabled, binding.cLayout::setEnabled)

        bindClickCallback(binding.cLayout, checkBox)
        bindCheckingChangeCallback(entity, entity.onCheckedChange, checkBox)

        UiBinderOfRecyclerForOtherType.bind(
            carrier = carrier,
            entity = entity.sideRecyclerEntity,
            recyclerView = binding.rvSideItems,
        )

        UiBinderOfRecyclerForOtherType.bind(
            carrier = carrier,
            entity = entity.bottomRecyclerEntity,
            recyclerView = binding.rvBottomItems,
        )
    }

    @JvmStatic
    private fun bindClickCallback(cLayout: ConstraintLayout, checkableButton: CompoundButton) {
        cLayout.setOnClickListener { checkableButton.toggle() }
    }

    @JvmStatic
    internal fun <D: Any> bindCheckingChangeCallback(
        entity: UiEntityOfCheckableButton<D>,
        changeCallback: BiConsumer<UiEntityOfCheckable<D>, Boolean>,
        view: CompoundButton
    ) {
        view.setOnCheckedChangeListener { _, isChecked -> changeCallback.accept(entity, isChecked) }
    }
}
