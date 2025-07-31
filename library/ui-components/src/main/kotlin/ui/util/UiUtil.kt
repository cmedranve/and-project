package pe.com.scotiabank.blpm.android.ui.util

import android.content.Context
import java.lang.IllegalStateException

/**
 * Created by Carlo Huaman on 13/08/2019.
 */
object UiUtil {
    @JvmStatic
    fun dpToPx(context: Context, dp: Double): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}
