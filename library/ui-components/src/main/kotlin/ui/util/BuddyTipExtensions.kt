package pe.com.scotiabank.blpm.android.ui.util

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.view.allViews
import com.scotiabank.canvascore.views.BuddyTip
import com.scotiabank.canvascore.views.CanvasTextView

fun BuddyTip.initializeClickable(
    spannableStringBuilder: SpannableStringBuilder,
    @DrawableRes drawableRes: Int
) {
    initialize(spannableStringBuilder, drawableRes)
    setUpCanvasTextViews(this)
}

fun setUpCanvasTextViews(buddyTip: BuddyTip) {
    val children: Sequence<View> = buddyTip.allViews
    children.forEach(::attemptSetMovementMethod)
}

private fun attemptSetMovementMethod(view: View) {
    if (view is CanvasTextView) {
        view.movementMethod = LinkMovementMethod.getInstance()
    }
}

fun BuddyTip.initialize(
    buddyTipMessage: String,
    @DrawableRes drawableRes: Int
) {
    initialize(SpannableStringBuilder(buddyTipMessage), drawableRes)
}

fun BuddyTip.initialize(
    spannableStringBuilder: SpannableStringBuilder,
    @DrawableRes drawableRes: Int
) {
    initializeNonExpandable(spannableStringBuilder)
    setBuddyTipIcon(drawableRes)
}
