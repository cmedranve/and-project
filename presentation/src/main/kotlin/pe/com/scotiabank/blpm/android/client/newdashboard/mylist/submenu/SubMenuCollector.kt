package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.submenu

import android.content.res.Resources
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.CollectorOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference

class SubMenuCollector(
    private val weakResources: WeakReference<Resources?>,
): CollectorOfTextButton {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?
    ): List<UiEntityOfTextButton<Any>> {

        val editEntity: UiEntityOfTextButton<Any> = UiEntityOfTextButton(
            paddingEntity = paddingEntity,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.edit).orEmpty(),
            receiver = receiver,
            data = SubMenuOption.EDIT,
            appearanceForEnabledState = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            drawableStartId = pe.com.scotiabank.blpm.android.ui.R.drawable.ic_edit,
            drawablePadding = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
        )
        val deleteEntity: UiEntityOfTextButton<Any> = UiEntityOfTextButton(
            paddingEntity = paddingEntity,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.delete).orEmpty(),
            receiver = receiver,
            data = SubMenuOption.DELETE,
            appearanceForEnabledState = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            drawableStartId = pe.com.scotiabank.blpm.android.ui.R.drawable.ic_delete,
            drawablePadding = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
        )

        return listOf(editEntity, deleteEntity)
    }
}