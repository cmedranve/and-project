package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbuttonloading

import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.buttons.NavigationButtonLoading
import pe.com.scotiabank.blpm.android.ui.databinding.ViewNavigationButtonLoadingItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfNavigationButtonLoading {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfNavigationButtonLoading<D>, ViewNavigationButtonLoadingItemBinding>,
    ) {
        val entity: UiEntityOfNavigationButtonLoading<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfNavigationButtonLoading<D>,
        binding: ViewNavigationButtonLoadingItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindState(entity, binding.nButtonLoading)
        bindContent(entity, binding.nButtonLoading)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, binding.nButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> bindState(
        entity: UiEntityOfNavigationButtonLoading<D>,
        nButtonLoading: NavigationButtonLoading,
    ) = when (entity.state) {
        NavigationButtonLoading.STATE_LOADING -> moveToLoading(entity, nButtonLoading)
        NavigationButtonLoading.STATE_COMPLETED -> moveToCompleted(entity, nButtonLoading)
        NavigationButtonLoading.STATE_ERROR -> moveToError(entity, nButtonLoading)
        else -> moveToIdle(entity, nButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> moveToLoading(
        entity: UiEntityOfNavigationButtonLoading<D>,
        nButtonLoading: NavigationButtonLoading,
    ) {
        if (NavigationButtonLoading.STATE_LOADING == nButtonLoading.getAnimatedButtonState()) return

        nButtonLoading.onStartAnimationButton(entity.textResId)
        returnToIdle(entity, nButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> returnToIdle(
        entity: UiEntityOfNavigationButtonLoading<D>,
        nButtonLoading: NavigationButtonLoading,
    ) {
        nButtonLoading.setAnimatedButtonState(NavigationButtonLoading.STATE_IDLE)
        entity.state = NavigationButtonLoading.STATE_IDLE
    }

    @JvmStatic
    private fun <D: Any> moveToCompleted(
        entity: UiEntityOfNavigationButtonLoading<D>,
        nButtonLoading: NavigationButtonLoading,
    ) {
        if (NavigationButtonLoading.STATE_COMPLETED == nButtonLoading.getAnimatedButtonState()) return

        nButtonLoading.onCompleteAnimationButton(entity.textResId)
        returnToIdle(entity, nButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> moveToError(
        entity: UiEntityOfNavigationButtonLoading<D>,
        nButtonLoading: NavigationButtonLoading,
    ) {
        if (NavigationButtonLoading.STATE_ERROR == nButtonLoading.getAnimatedButtonState()) return
        if (ResourcesCompat.ID_NULL == entity.errorDrawableId) return

        nButtonLoading.onErrorAnimationButton(entity.textResId, entity.errorDrawableId)
        returnToIdle(entity, nButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> moveToIdle(
        entity: UiEntityOfNavigationButtonLoading<D>,
        nButtonLoading: NavigationButtonLoading,
    ) {
        if (NavigationButtonLoading.STATE_IDLE == nButtonLoading.getAnimatedButtonState()) return

        returnToIdle(entity, nButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> bindContent(
        entity: UiEntityOfNavigationButtonLoading<D>,
        nButtonLoading: NavigationButtonLoading,
    ) {
        nButtonLoading.isEnabled = entity.isEnabled
        nButtonLoading.setTextAnimatedButton(entity.textResId)
    }
}
