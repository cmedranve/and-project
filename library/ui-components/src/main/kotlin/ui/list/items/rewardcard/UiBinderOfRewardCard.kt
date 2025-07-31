package pe.com.scotiabank.blpm.android.ui.list.items.rewardcard

import pe.com.scotiabank.blpm.android.ui.databinding.ViewRewardCardItemBinding
import pe.com.scotiabank.blpm.android.ui.rewardcard.RewardCard
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfRewardCard {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfRewardCard<D>, ViewRewardCardItemBinding>
    ) {
        val entity: UiEntityOfRewardCard<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfRewardCard<D>, binding: ViewRewardCardItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindCardView(entity, binding.eraserCard)
        bindEraserCardContent(entity, binding.eraserCard)
    }

    @JvmStatic
    private fun <D: Any> bindCardView(entity: UiEntityOfRewardCard<D>, atmCard: RewardCard) {
        UiBinderOfClickCallback.bindNonClickableOrClickable(entity, entity.receiver, atmCard)
    }

    @JvmStatic
    private fun <D: Any> bindEraserCardContent(entity: UiEntityOfRewardCard<D>, rewardCard: RewardCard) {
        rewardCard.setIsMainStyle(entity.isMainStyle)
        rewardCard.setLabelPoints(entity.labelPoints)
        rewardCard.setPoints(entity.points)
        rewardCard.setPointsEquivalence(entity.pointsEquivalence)
        rewardCard.setPointsRate(entity.pointsRate)
        rewardCard.setLogo(entity.iconRes)
    }
}
