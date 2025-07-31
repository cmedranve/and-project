package pe.com.scotiabank.blpm.android.client.base.canvassnackbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

class SnackbarAction(
    @DrawableRes val image: Int = ResourcesCompat.ID_NULL,
    @StringRes val text: Int,
    val isPositionBottom: Boolean = false,
    val isPositionRight: Boolean = false,
)
