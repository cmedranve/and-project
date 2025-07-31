package pe.com.scotiabank.blpm.android.client.base.point

import android.content.res.Resources
import android.icu.text.NumberFormat
import androidx.annotation.PluralsRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.lang.ref.WeakReference

class PointFormatter(
    private val integerNumberFormat: NumberFormat,
    private val weakResources: WeakReference<Resources?>,
) {

    fun format(rawPoints: Int): String {

        @PluralsRes val pluralsResId: Int = R.plurals.points
        val pointAcronym = weakResources.get()
            ?.getQuantityString(pluralsResId, rawPoints)
            .orEmpty()

        val pointsWithSeparator: String = integerNumberFormat.format(rawPoints)
        return String.format(Constant.X_X_PATTERN, pointsWithSeparator, pointAcronym)
    }
}
