package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading

import com.scotiabank.canvascore.buttons.CanvasButtonLoading
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCanvasButtonLoadingItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfCanvasButtonLoading {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCanvasButtonLoading<D>, ViewCanvasButtonLoadingItemBinding>,
    ) {
        val entity: UiEntityOfCanvasButtonLoading<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfCanvasButtonLoading<D>,
        binding: ViewCanvasButtonLoadingItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindContent(entity, binding.cButtonLoading)
        bindState(entity, binding.cButtonLoading)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, binding.cButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> bindContent(
        entity: UiEntityOfCanvasButtonLoading<D>,
        cButtonLoading: CanvasButtonLoading,
    ) {
        cButtonLoading.isEnabled = entity.isEnabled
        cButtonLoading.setTextButtonLoading(entity.text)
    }

    @JvmStatic
    private fun <D: Any> bindState(
        entity: UiEntityOfCanvasButtonLoading<D>,
        cbl: CanvasButtonLoading,
    ) {
        if (CanvasButtonLoading.STATE_LOADING == entity.state) {
            moveToNonIdle(entity, cbl, CanvasButtonLoading.STATE_LOADING, cbl::onStartButtonLoading)
            return
        }
        if (CanvasButtonLoading.STATE_SUCCESS == entity.state) {
            moveToNonIdle(entity, cbl, CanvasButtonLoading.STATE_SUCCESS, cbl::onSuccessButtonLoading)
            return
        }
        if (CanvasButtonLoading.STATE_ERROR == entity.state) {
            moveToNonIdle(entity, cbl, CanvasButtonLoading.STATE_ERROR, cbl::onErrorButtonLoading)
            return
        }
        moveToIdle(entity, cbl)
    }

    @JvmStatic
    private inline fun <D: Any> moveToNonIdle(
        entity: UiEntityOfCanvasButtonLoading<D>,
        cButtonLoading: CanvasButtonLoading,
        matchingState: Int,
        callback: () -> Unit,
    ) {
        if (matchingState == cButtonLoading.getButtonLoadingState()) return

        callback.invoke()
        returnToIdle(entity, cButtonLoading)
    }

    @JvmStatic
    private fun <D: Any> returnToIdle(
        entity: UiEntityOfCanvasButtonLoading<D>,
        cButtonLoading: CanvasButtonLoading,
    ) {
        cButtonLoading.setStateButtonLoading(CanvasButtonLoading.STATE_IDLE)
        entity.state = CanvasButtonLoading.STATE_IDLE
    }

    @JvmStatic
    private fun <D: Any> moveToIdle(
        entity: UiEntityOfCanvasButtonLoading<D>,
        cButtonLoading: CanvasButtonLoading,
    ) {
        if (CanvasButtonLoading.STATE_IDLE == cButtonLoading.getButtonLoadingState()) return

        returnToIdle(entity, cButtonLoading)
    }
}
