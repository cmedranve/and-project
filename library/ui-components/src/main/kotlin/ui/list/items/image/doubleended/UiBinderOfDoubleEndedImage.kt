package pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended

import pe.com.scotiabank.blpm.android.ui.databinding.ViewDoubleEndedImageItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfSideLinearLayout
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.UiBinderOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiBinderOfRecyclerForOtherType
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfDoubleEndedImage {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfDoubleEndedImage<D>, ViewDoubleEndedImageItemBinding>
    ) {
        val entity: UiEntityOfDoubleEndedImage<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        carrier: UiEntityCarrier<UiEntityOfDoubleEndedImage<D>, ViewDoubleEndedImageItemBinding>,
        entity: UiEntityOfDoubleEndedImage<D>,
        binding: ViewDoubleEndedImageItemBinding,
    ) {
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)

        UiBinderOfSideLinearLayout.bind(
            paddingEntity = entity.paddingEntityOfLeftImage,
            bias = entity.verticalBiasOfLeftImage,
            llSide = binding.llLeft,
        )
        UiBinderOfOneColumnImage.attemptBindDrawable(entity.leftDrawableId, binding.ivLeft)

        UiBinderOfSideLinearLayout.bind(
            paddingEntity = entity.paddingEntityOfRightImage,
            bias = entity.verticalBiasOfRightImage,
            llSide = binding.llRight,
        )
        UiBinderOfOneColumnImage.attemptBindDrawable(entity.rightDrawableId, binding.ivRight)

        UiBinderOfClickCallback.bindNonClickableOrClickableBackground(entity, entity.receiver, binding.root)

        UiBinderOfRecyclerForOtherType.bind(
            carrier = carrier,
            entity = entity.centerRecyclerEntity,
            recyclerView = binding.rvCenterItems,
        )
    }
}
