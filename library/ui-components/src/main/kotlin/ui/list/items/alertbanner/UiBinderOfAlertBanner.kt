package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.databinding.ViewAlertBannerItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfAlertBanner {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfAlertBanner<D>, ViewAlertBannerItemBinding>
    ) {
        val entity: UiEntityOfAlertBanner<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfAlertBanner<D>,
        binding: ViewAlertBannerItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        val receiver: InstanceReceiver? = entity.receiver
        if (receiver == null) bindNonCloseable(entity, binding) else bindCloseable(entity, binding, receiver)
    }

    @JvmStatic
    private fun <D: Any> bindNonCloseable(
        entity: UiEntityOfAlertBanner<D>,
        binding: ViewAlertBannerItemBinding,
    ) {
        binding.abInfo.setAttributes(
            textContent = entity.textContent,
            iconContentDescription = entity.iconContentDescription,
            type = entity.type,
            newTypeText = entity.newTypeText,
            supportLink = entity.supportLink,
            emphasisContent = entity.emphasisContent,
            emphasisLink = entity.emphasisLink,
        )
    }

    @JvmStatic
    private fun <D: Any> bindCloseable(
        entity: UiEntityOfAlertBanner<D>,
        binding: ViewAlertBannerItemBinding,
        receiver: InstanceReceiver,
    ) {
        binding.abInfo.setAttributes(
            textContent = entity.textContent,
            iconContentDescription = entity.iconContentDescription,
            type = entity.type,
            newTypeText = entity.newTypeText,
            supportLink = entity.supportLink,
            emphasisContent = entity.emphasisContent,
            emphasisLink = entity.emphasisLink,
        ) {
            receiver.receive(entity)
        }
    }
}
