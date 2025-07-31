package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.buttonlesscard

import com.google.android.gms.common.util.BiConsumer
import com.google.android.material.card.MaterialCardView
import pe.com.scotiabank.blpm.android.ui.databinding.ViewButtonLessCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiBinderOfRecyclerForOtherType
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.UiBinderOfCheckableCard
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfButtonLessCard
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfButtonLessCard {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfButtonLessCard<D>, ViewButtonLessCardItemBinding>
    ) {
        val entity: UiEntityOfButtonLessCard<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        carrier: UiEntityCarrier<UiEntityOfButtonLessCard<D>, ViewButtonLessCardItemBinding>,
        entity: UiEntityOfButtonLessCard<D>,
        binding: ViewButtonLessCardItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)

        binding.mcv.setOnCheckedChangeListener(null)
        binding.mcv.setOnClickListener(null)

        UiBinderOfCheckableCard.bind(entity.isEnabled, entity.isChecked, binding.mcv)

        bindClickCallback(binding.mcv)
        bindCheckingChangeCallback(entity, entity.onCheckedChange, binding.mcv)

        UiBinderOfRecyclerForOtherType.bind(
            carrier = carrier,
            entity = entity.recyclerEntity,
            recyclerView = binding.rvItems,
        )
    }

    @JvmStatic
    private fun <D: Any> bindCheckingChangeCallback(
        entity: UiEntityOfButtonLessCard<D>,
        changeCallback: BiConsumer<UiEntityOfCheckable<D>, Boolean>,
        mcv: MaterialCardView,
    ) {
        mcv.setOnCheckedChangeListener { _, isChecked -> changeCallback.accept(entity, isChecked) }
    }

    @JvmStatic
    private fun bindClickCallback(checkableCard: MaterialCardView) {
        checkableCard.setOnClickListener { onCardClicked(checkableCard) }
    }

    @JvmStatic
    private fun onCardClicked(checkableCard: MaterialCardView) {
        if (checkableCard.isChecked) return

        checkableCard.toggle()
    }
}
