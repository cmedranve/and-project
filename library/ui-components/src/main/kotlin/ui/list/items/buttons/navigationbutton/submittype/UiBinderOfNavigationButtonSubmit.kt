package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.submittype

import android.view.Gravity
import pe.com.scotiabank.blpm.android.ui.databinding.ViewNavigationButtonSubmitItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiBinderOfNavigationButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiEntityOfNavigationButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfNavigationButtonSubmit {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfNavigationButton<D>, ViewNavigationButtonSubmitItemBinding>,
    ) {
        val entity: UiEntityOfNavigationButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfNavigationButton<D>,
        binding: ViewNavigationButtonSubmitItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        UiBinderOfNavigationButton.bindContent(entity, binding.nButtonSubmit, Gravity.END)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, binding.nButtonSubmit)
    }
}
