package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.labelbutton.FactoryOfLabelButtonEntity
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.Intention
import pe.com.scotiabank.blpm.android.client.base.quantitytext.UpdaterOfQuantityText
import pe.com.scotiabank.blpm.android.client.base.quantitytext.TextBuilderForQuantity
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.ui.list.items.labelbuttonpair.AdapterFactoryOfLabelButtonPair
import pe.com.scotiabank.blpm.android.ui.list.items.labelbuttonpair.UiEntityOfLabelButtonPair
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.lang.ref.WeakReference

class LabelButtonComposerForSuccessState(
    paddingEntity: UiEntityOfPadding,
    private val weakResources: WeakReference<Resources?>,
    receiver: InstanceReceiver,
    private val templateForAddingRecentPayments: OptionTemplate,
    private val builder: TextBuilderForQuantity,
) : UpdaterOfQuantityText {

    private val paddingEntity = UiEntityOfPadding(
        left = paddingEntity.left,
        right = paddingEntity.right,
        top = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
        bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
    )

    private val factoryOfLabelButtonEntity = FactoryOfLabelButtonEntity(receiver)
    private val entity: UiEntityOfLabelButtonPair<Intention> = createUiEntity()
    private val labelEntity: UiEntityOfText
        get() = entity.labelEntity

    private fun createUiEntity(): UiEntityOfLabelButtonPair<Intention> {

        if (templateForAddingRecentPayments.isVisible) {

            return factoryOfLabelButtonEntity.create(
                paddingEntity = this.paddingEntity,
                labelAppearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
                buttonText = weakResources.get()?.getString(R.string.add_action).orEmpty(),
                data = Intention.GO_ADD_PAYMENTS,
                drawableStartId = R.drawable.ic_rounded_plus
            )
        }

        return factoryOfLabelButtonEntity.create(
            paddingEntity = this.paddingEntity,
            labelAppearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
            isEnabled = false,
            data = Intention.GO_ADD_PAYMENTS,
        )
    }

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfLabelButtonPair<Intention>> {

        val entities: List<UiEntityOfLabelButtonPair<Intention>> = listOf(entity)
        val adapterFactory: AdapterFactoryOfLabelButtonPair<Intention> = AdapterFactoryOfLabelButtonPair()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun updateTextWith(quantity: Int) {
        labelEntity.text = builder.build(quantity)
    }
}