package pe.com.scotiabank.blpm.android.client.base.canvasintroslider

import com.scotiabank.canvascore.dialog.model.IntroSliderModel
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong

class CanvasIntroSliderDataHolder(
    val introSliders: ArrayList<IntroSliderModel>,
    val modalTalkbackContent: String,
    val pageIndicatorTalkbackContent: String,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    val id: Long = randomLong(),
)
