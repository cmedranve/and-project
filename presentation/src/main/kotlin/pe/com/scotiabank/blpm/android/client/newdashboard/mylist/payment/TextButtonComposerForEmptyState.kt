package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import android.content.res.Resources
import android.view.Gravity
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.Intention
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.AdapterFactoryOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.lang.ref.WeakReference

class TextButtonComposerForEmptyState(
    paddingEntity: UiEntityOfPadding,
    private val weakResources: WeakReference<Resources?>,
    private val receiver: InstanceReceiver,
    private val templateForAddingRecentPayments: OptionTemplate,
) {

    private val paddingEntity = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
    )

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfTextButton<Intention>> {

        val entities: List<UiEntityOfTextButton<Intention>> = collect()
        val adapterFactory: AdapterFactoryOfTextButton<Intention> = AdapterFactoryOfTextButton()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    private fun collect(): List<UiEntityOfTextButton<Intention>> {

        if (templateForAddingRecentPayments.isVisible.not()) return emptyList()

        val entity: UiEntityOfTextButton<Intention> = UiEntityOfTextButton(
            paddingEntity = paddingEntity,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.my_list_new_payment_frequent_button).orEmpty(),
            receiver = receiver,
            data = Intention.GO_ADD_PAYMENTS,
            gravity = Gravity.CENTER,
            drawableStartId = R.drawable.ic_rounded_plus
        )

        return listOf(entity)
    }
}