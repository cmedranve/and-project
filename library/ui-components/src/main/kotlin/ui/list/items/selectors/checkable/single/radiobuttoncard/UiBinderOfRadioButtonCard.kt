package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobuttoncard

import android.widget.CompoundButton
import com.google.android.gms.common.util.BiConsumer
import com.google.android.material.card.MaterialCardView
import pe.com.scotiabank.blpm.android.ui.databinding.ViewRadioButtonCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfSideLinearLayout
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiBinderOfRecyclerForOtherType
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UpdaterOfConstraintLayoutParams
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.UiBinderOfCheckableCard
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.UiBinderOfCompoundButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfRadioButtonCard {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonCardItemBinding>
    ) {
        val entity: UiEntityOfCheckableButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonCardItemBinding>,
        entity: UiEntityOfCheckableButton<D>,
        binding: ViewRadioButtonCardItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfPadding.bind(entity.paddingEntityOfCheckableIcon, binding.llCrb)

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
            llSide = binding.llCrb,
        )

        binding.crb.setOnCheckedChangeListener(null)
        binding.mcv.setOnCheckedChangeListener(null)
        binding.mcv.setOnClickListener(null)

        UiBinderOfCompoundButton.bind(entity.isEnabled, entity.isChecked, binding.crb)
        UiBinderOfCheckableCard.bind(entity.isEnabled, entity.isChecked, binding.mcv)

        bindClickCallback(binding.mcv, binding.crb)
        bindCheckingChangeCallback(entity, entity.onCheckedChange, binding.mcv)

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
    private fun <D: Any> bindCheckingChangeCallback(
        entity: UiEntityOfCheckableButton<D>,
        changeCallback: BiConsumer<UiEntityOfCheckable<D>, Boolean>,
        mcv: MaterialCardView,
    ) {
        mcv.setOnCheckedChangeListener { _, isChecked -> changeCallback.accept(entity, isChecked) }
    }

    @JvmStatic
    private fun bindClickCallback(checkableCard: MaterialCardView, checkableButton: CompoundButton) {
        checkableCard.setOnClickListener { onCardClicked(checkableCard, checkableButton) }
    }

    @JvmStatic
    private fun onCardClicked(checkableCard: MaterialCardView, checkableButton: CompoundButton) {
        if (checkableCard.isChecked) return

        checkableCard.toggle()
        checkableButton.isChecked = checkableCard.isChecked
    }
}
