package pe.com.scotiabank.blpm.android.ui.list.items

import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

object UiBinderOfClickCallback {

    /**
     * Expected public invocation from a non-button-based view. Its click-listening require setting
     * isClickable and isFocusable flags to activate/deactivate it.
     *
     * E.g. AtmCard, Avatar, Card, Image, QuickActionCard and RewardCard.
     * */
    @JvmStatic
    fun bindNonClickableOrClickable(any: Any, receiver: InstanceReceiver?, view: View) {
        if (receiver == null) bindNonClickable(view) else bindClickable(any, receiver, view)
    }

    @JvmStatic
    private fun bindNonClickable(view: View) {
        bindIfDifferent(false, view::isClickable, view::setClickable)
        bindIfDifferent(false, view::isFocusable, view::setFocusable)
    }

    @JvmStatic
    private fun bindClickable(any: Any, receiver: InstanceReceiver?, view: View) {
        bindIfDifferent(true, view::isClickable, view::setClickable)
        bindIfDifferent(true, view::isFocusable, view::setFocusable)

        attemptBindEnabledClickable(any, receiver, view)
    }

    /**
     * Expected public invocation from a button-based view. Its click-listening require just setting
     * isEnabled flag to enable/disable it. The other flags, isClickable and isFocusable, aren't
     * required to be set.
     *
     * E.g. CanvasButton, NavigationButton, QuickActionButton and TextButton.
     * */
    @JvmStatic
    fun attemptBindEnabledClickable(any: Any?, receiver: InstanceReceiver?, view: View) {
        val anyNonNull: Any = any ?: return
        val nonNullReceiver: InstanceReceiver = receiver ?: return

        bind(anyNonNull, nonNullReceiver, view)
    }

    @JvmStatic
    private fun bind(anyNonNull: Any, receiver: InstanceReceiver, view: View) {
        view.setOnClickListener { receiver.receive(anyNonNull) }
    }

    /**
     * Expected public invocation from a stateless-background-based view. Its click-listening require
     * setting isClickable and isFocusable flags to activate/deactivate it.
     *
     * If it's clickable, the background requires android.R.attr.selectableItemBackground.
     * Otherwise, android.R.color.transparent.
     *
     * For example, LeadingAvatar, DoubleEndedImage and QuickAction, Text.
     * */
    @JvmStatic
    fun bindNonClickableOrClickableBackground(any: Any, receiver: InstanceReceiver?, view: View) {
        if (receiver == null) bindNonClickableBackground(view) else bindClickableBackground(any, receiver, view)
    }

    @JvmStatic
    private fun bindNonClickableBackground(view: View) {
        bindNonClickable(view)
        @ColorInt val color: Int = ContextCompat.getColor(
            view.context,
            android.R.color.transparent,
        )
        view.setBackgroundColor(color)
    }

    @JvmStatic
    private fun bindClickableBackground(any: Any, receiver: InstanceReceiver?, view: View) {
        bindClickable(any, receiver, view)
        val outValue = TypedValue()
        view.context.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true,
        )
        view.setBackgroundResource(outValue.resourceId)
    }
}
