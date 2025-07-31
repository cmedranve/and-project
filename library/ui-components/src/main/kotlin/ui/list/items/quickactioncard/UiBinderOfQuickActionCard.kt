package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.cards.QuickActionCard
import com.scotiabank.canvascore.views.StatusBadge
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQuickActionCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiBinderOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.statusbadge.UiEntityOfStatusBadge
import pe.com.scotiabank.blpm.android.ui.list.items.statusbadge.attemptBindStatusBadge
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfQuickActionCard {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfQuickActionCard<D>, ViewQuickActionCardItemBinding>
    ) {
        val entity: UiEntityOfQuickActionCard<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfQuickActionCard<D>,
        binding: ViewQuickActionCardItemBinding
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindCard(entity, binding.qac)
        UiBinderOfClickCallback.bindNonClickableOrClickable(entity, entity.receiver, binding.qac)
        bindStatusBadge(entity.statusBadgeEntity, binding.qac)
    }

    @JvmStatic
    private fun <D: Any> bindCard(entity: UiEntityOfQuickActionCard<D>, qac: QuickActionCard) {
        bindDescriptionWithIcon(entity, qac)
        UiBinderOfChevron.attemptBind(entity.chevronEntity, qac)
        bindDescriptionSecondary(entity, qac)
        UiBinderOfCard.bindElevation(entity.elevationRes, qac)
        UiBinderOfCard.bindStrokeWidth(R.dimen.canvascore_border_width_1, qac)
        entity.borderStyleOfCard?.setUp(qac)
    }

    @JvmStatic
    private fun <D: Any> bindDescriptionWithIcon(
        entity: UiEntityOfQuickActionCard<D>,
        qac: QuickActionCard
    ) {
        if (ResourcesCompat.ID_NULL == entity.iconRes) {
            val imgCardIcon: ImageView = qac.findViewById(R.id.img_card_icon)
            imgCardIcon.visibility = View.GONE
        } else {
            qac.setQuickActionCardIcon(entity.iconRes)
        }
        qac.setQuickActionCardDescription(entity.description)
    }

    @JvmStatic
    private fun <D: Any> bindDescriptionSecondary(
        entity: UiEntityOfQuickActionCard<D>,
        qac: QuickActionCard
    ) {
        qac.setQuickActionCardDescriptionSecondary(entity.placeholderSecondaryRes)
        if (entity.showLoadingShimmer) {
            qac.setHasTransientState(true)
            qac.enableLoadingShimmer()
            return
        }

        qac.disableLoadingShimmer()
        qac.setHasTransientState(false)
        qac.setQuickActionCardDescriptionSecondary(entity.descriptionSecondary)
    }

    @JvmStatic
    private fun bindStatusBadge(
        statusBadgeEntity: UiEntityOfStatusBadge?,
        qac: QuickActionCard
    ) {
        if (statusBadgeEntity == null) {
            val statusBadge: StatusBadge = qac.findViewById(R.id.status_badge_view)
            statusBadge.visibility = View.GONE
            return
        }
        attemptBindStatusBadge(statusBadgeEntity, qac::setStatus)
    }
}
