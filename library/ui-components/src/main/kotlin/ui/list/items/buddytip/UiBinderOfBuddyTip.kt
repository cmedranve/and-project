package pe.com.scotiabank.blpm.android.ui.list.items.buddytip

import android.view.View
import android.widget.ImageView
import android.widget.TextSwitcher
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.views.BuddyTip
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.databinding.ViewBuddyTipItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.setUpCanvasTextViews

object UiBinderOfBuddyTip {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfBuddyTip, ViewBuddyTipItemBinding>) {
        val entity: UiEntityOfBuddyTip = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfBuddyTip, binding: ViewBuddyTipItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        removeClickCallbacks(binding.btInfo)
        bindByType(entity, binding.btInfo)
        bindBuddyTipIcon(entity, binding.btInfo)
        showShimmerIfNeeded(entity, binding.btInfo)
        bindBackground(entity, binding.btInfo)
    }

    private fun removeClickCallbacks(btInfo: BuddyTip) {
        btInfo.setOnClickListener(null)
        val textSwitcherBuddyTip: TextSwitcher = btInfo.findViewById(R.id.text_switcher_buddy_tip)
        textSwitcherBuddyTip.setOnClickListener(null)
    }

    private fun bindByType(entity: UiEntityOfBuddyTip, btInfo: BuddyTip) {
        when (entity.type) {
            BuddyTipType.NON_EXPANDABLE -> bindNonExpandable(entity, btInfo)
            BuddyTipType.EXPANDABLE -> bindExpandable(entity, btInfo)
            BuddyTipType.DISMISSIBLE -> bindDismissible(entity, btInfo)
        }
    }

    private fun bindNonExpandable(entity: UiEntityOfBuddyTip, btInfo: BuddyTip) {
        hideCloseIcon(btInfo)
        btInfo.initializeNonExpandable(description = entity.descriptionBuilder)
        setUpCanvasTextViews(btInfo)
    }

    private fun hideCloseIcon(btInfo: BuddyTip) {
        val imgBuddyTipClose: ImageView = btInfo.findViewById(R.id.imgBuddyTipClose)
        imgBuddyTipClose.visibility = View.GONE
    }

    private fun bindExpandable(entity: UiEntityOfBuddyTip, btInfo: BuddyTip) {
        hideCloseIcon(btInfo)
        val textSwitcherBuddyTip: TextSwitcher = btInfo.findViewById(R.id.text_switcher_buddy_tip)
        textSwitcherBuddyTip.isClickable = false
        btInfo.setLearnMoreListener { isExpanded -> onExpandableClicked(isExpanded, entity) }
        btInfo.initializeExpandable(
            description = entity.descriptionBuilder,
            expandedDescription = entity.expandedDescriptionBuilder,
            accessibilityActionLabel = entity.accessibilityActionLabel,
            accessibilityDescription = entity.accessibilityDescription,
            expandedAccessibilityDescription = entity.expandedAccessibilityDescription,
        )
    }

    private fun onExpandableClicked(isExpanded: Boolean, entity: UiEntityOfBuddyTip) {
        val receiver: InstanceReceiver = entity.receiver ?: return

        val event: BuddyTipEvent = if (isExpanded) BuddyTipEvent.EXPANDED else BuddyTipEvent.COLLAPSED
        val carrier = BuddyTipEventCarrier(event, entity)
        receiver.receive(carrier)
    }

    private fun bindDismissible(entity: UiEntityOfBuddyTip, btInfo: BuddyTip) {
        btInfo.initializeDismissible(
            description = entity.descriptionBuilder,
            closeButtonLabel = entity.closeButtonLabel,
            onClickListener = { onClicked(entity) },
            onCloseListener = { onClosed(entity) },
        )
    }

    private fun onClicked(entity: UiEntityOfBuddyTip) {
        val receiver: InstanceReceiver = entity.receiver ?: return

        val carrier = BuddyTipEventCarrier(BuddyTipEvent.CLICKED, entity)
        receiver.receive(carrier)
    }

    private fun onClosed(entity: UiEntityOfBuddyTip) {
        val receiver: InstanceReceiver = entity.receiver ?: return

        val carrier = BuddyTipEventCarrier(BuddyTipEvent.CLOSED, entity)
        receiver.receive(carrier)
    }

    private fun bindBuddyTipIcon(entity: UiEntityOfBuddyTip, btInfo: BuddyTip) {
        val imgBuddyTipIcon: ImageView = btInfo.findViewById(R.id.img_buddy_tip_icon)
        if (ResourcesCompat.ID_NULL == entity.iconRes) {
            imgBuddyTipIcon.visibility = View.GONE
            return
        }
        btInfo.setBuddyTipIcon(entity.iconRes)
        imgBuddyTipIcon.visibility = View.VISIBLE
    }

    private fun showShimmerIfNeeded(entity: UiEntityOfBuddyTip, btInfo: BuddyTip) {
        if (entity.showLoadingShimmer) btInfo.showShimmer() else btInfo.hideShimmer()
    }

    private fun bindBackground(entity: UiEntityOfBuddyTip, btInfo: BuddyTip) {
        if (entity.isBackgroundEmpty) {
            btInfo.setBackgroundEmpty()
            return
        }
        @DrawableRes val backgroundRes: Int = R.drawable.canvascore_background_card_dashed
        btInfo.background = AppCompatResources.getDrawable(btInfo.context, backgroundRes)
    }
}
