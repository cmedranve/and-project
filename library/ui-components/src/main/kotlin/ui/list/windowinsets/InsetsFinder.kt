package pe.com.scotiabank.blpm.android.ui.list.windowinsets

import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type

internal object InsetsFinder {

    private val typesToFind: Int
        @JvmStatic
        get() = Type.systemBars() or Type.displayCutout()

    @JvmStatic
    fun findInsets(windowInsets: WindowInsetsCompat): Insets = windowInsets.getInsets(typesToFind)
}
