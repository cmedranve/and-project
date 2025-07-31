package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.continuetype

import android.view.Gravity
import pe.com.scotiabank.blpm.android.ui.databinding.ViewNavigationButtonContinueItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiBinderOfNavigationButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiEntityOfNavigationButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfNavigationButtonContinue {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfNavigationButton<D>, ViewNavigationButtonContinueItemBinding>,
    ) {
        val entity: UiEntityOfNavigationButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfNavigationButton<D>,
        binding: ViewNavigationButtonContinueItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        UiBinderOfNavigationButton.bindContent(entity, binding.nButtonContinue, Gravity.END)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, binding.nButtonContinue)
    }
}
