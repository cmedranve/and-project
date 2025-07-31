package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.informativebottomsheet

import android.content.res.Resources
import android.view.Gravity
import androidx.annotation.StringRes
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.twocolumntext.FactoryOfTwoColumnEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.CollectorOfTwoColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText
import java.lang.ref.WeakReference

class CollectorOfTwoColumnText(
    private val weakResources: WeakReference<Resources?>,
    private val factory: FactoryOfTwoColumnEntity,
) : CollectorOfTwoColumnText {

    private val paddingEntityOfLastItem: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
        )
    }

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?
    ): List<UiEntityOfTwoColumnText> {

        val firstItem = createItemTwoColumn(
            paddingEntity = paddingEntity,
            textResource = R.string.dynamic_cvv_information_item_one
        )

        val secondItem = createItemTwoColumn(
            paddingEntity = paddingEntity,
            textResource = R.string.dynamic_cvv_information_item_two
        )

        val thirdItem = createItemTwoColumn(
            paddingEntity = paddingEntityOfLastItem,
            textResource = R.string.dynamic_cvv_information_item_three
        )

        return listOf(firstItem, secondItem, thirdItem)
    }

    private fun createItemTwoColumn(
        paddingEntity: UiEntityOfPadding,
        @StringRes textResource: Int,
    ): UiEntityOfTwoColumnText = factory.create(
        paddingEntity = paddingEntity,
        appearance1 = com.scotiabank.canvascore.R.style.canvascore_style_caption,
        text1 = DOT,
        gravity1 = Gravity.END,
        appearance2 = com.scotiabank.canvascore.R.style.canvascore_style_caption,
        text2 = weakResources.get()?.getString(textResource).orEmpty(),
        gravity2 = Gravity.START,
        guidelinePercent = GUIDELINE_FOR_NUMBER_OF_LIST
    )

    companion object {

        private const val DOT = "•"

        private val GUIDELINE_FOR_NUMBER_OF_LIST: Float
            get() = 0.05f
    }
}
