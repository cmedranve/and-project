package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.checkboxcard

import android.widget.CheckBox
import android.widget.CompoundButton
import com.google.android.material.card.MaterialCardView
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCheckBoxCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfSideLinearLayout
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiBinderOfRecyclerForOtherType
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UpdaterOfConstraintLayoutParams
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.UiBinderOfCheckableCard
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.UiBinderOfCompoundButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.checkbox.UiBinderOfCheckBox
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfCheckBoxCard {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxCardItemBinding>
    ) {
        val entity: UiEntityOfCheckableButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxCardItemBinding>,
        entity: UiEntityOfCheckableButton<D>,
        binding: ViewCheckBoxCardItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfPadding.bind(entity.paddingEntityOfCheckableIcon, binding.llCcb)

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
        binding.mcv.setOnCheckedChangeListener(null)
        binding.mcv.setOnClickListener(null)

        UiBinderOfCompoundButton.bind(entity.isEnabled, entity.isChecked, checkBox)
        UiBinderOfCheckableCard.bind(entity.isEnabled, entity.isChecked, binding.mcv)

        bindClickCallback(binding.mcv, checkBox)
        UiBinderOfCheckBox.bindCheckingChangeCallback(entity, entity.onCheckedChange, checkBox)

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
    private fun bindClickCallback(checkableCard: MaterialCardView, checkableButton: CompoundButton) {
        checkableCard.setOnClickListener { onCardClicked(checkableCard, checkableButton) }
    }

    @JvmStatic
    private fun onCardClicked(checkableCard: MaterialCardView, checkableButton: CompoundButton) {
        checkableCard.toggle()
        checkableButton.isChecked = checkableCard.isChecked
    }
}
