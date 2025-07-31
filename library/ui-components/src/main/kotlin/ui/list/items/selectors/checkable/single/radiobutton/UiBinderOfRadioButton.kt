package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobutton

import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.common.util.BiConsumer
import pe.com.scotiabank.blpm.android.ui.databinding.ViewRadioButtonItemBinding
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

object UiBinderOfRadioButton {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonItemBinding>
    ) {
        val entity: UiEntityOfCheckableButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonItemBinding>,
        entity: UiEntityOfCheckableButton<D>,
        binding: ViewRadioButtonItemBinding
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
        binding.cLayout.setOnClickListener(null)

        UiBinderOfCompoundButton.bind(entity.isEnabled, entity.isChecked, binding.crb)
        bindIfDifferent(entity.isEnabled, binding.cLayout::isEnabled, binding.cLayout::setEnabled)

        bindClickCallback(entity, entity.onCheckedChange, binding.cLayout, binding.crb)

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
    private fun <D: Any> bindClickCallback(
        entity: UiEntityOfCheckableButton<D>,
        changeCallback: BiConsumer<UiEntityOfCheckable<D>, Boolean>,
        cLayout: ConstraintLayout,
        checkableButton: CompoundButton,
    ) {
        cLayout.setOnClickListener { onLayoutClicked(entity, changeCallback, checkableButton) }
    }

    @JvmStatic
    private fun <D: Any> onLayoutClicked(
        entity: UiEntityOfCheckableButton<D>,
        changeCallback: BiConsumer<UiEntityOfCheckable<D>, Boolean>,
        checkableButton: CompoundButton,
    ) {
        if (checkableButton.isChecked) return

        checkableButton.isChecked = true
        changeCallback.accept(entity, true)
    }
}
