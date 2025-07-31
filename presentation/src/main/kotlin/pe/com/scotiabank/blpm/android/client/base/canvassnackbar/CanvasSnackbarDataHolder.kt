package pe.com.scotiabank.blpm.android.client.base.canvassnackbar

import android.text.SpannableStringBuilder
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong

class CanvasSnackbarDataHolder(
    @DrawableRes val icon: Int = ResourcesCompat.ID_NULL,
    val message: SpannableStringBuilder,
    val action: SnackbarAction? = null,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    val id: Long = randomLong(),
)
