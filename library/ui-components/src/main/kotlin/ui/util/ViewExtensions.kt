package pe.com.scotiabank.blpm.android.ui.util

import android.view.View
import androidx.core.util.Consumer
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import androidx.core.view.doOnLayout

fun View.setCallbackOnKeyboardVisibilityChanged(
    callback: Consumer<StateOfKeyboardVisibility>
) = doOnLayout { view ->

    val rootInsets: WindowInsetsCompat = ViewCompat.getRootWindowInsets(view) ?: return@doOnLayout
    @InsetsType val ime: Int = WindowInsetsCompat.Type.ime()

    var isOldVisible: Boolean = rootInsets.isVisible(ime)
    callback.accept(
        StateOfKeyboardVisibility(isOldVisible = isOldVisible, isNewVisible = isOldVisible)
    )

    val listener = OnApplyWindowInsetsListener { _, insets ->

        val isNewVisible: Boolean = insets.isVisible(ime)
        if (isOldVisible != isNewVisible) {
            callback.accept(
                StateOfKeyboardVisibility(isOldVisible = isOldVisible, isNewVisible = isNewVisible)
            )
        }
        isOldVisible = isNewVisible

        return@OnApplyWindowInsetsListener insets
    }

    ViewCompat.setOnApplyWindowInsetsListener(view, listener)
}
