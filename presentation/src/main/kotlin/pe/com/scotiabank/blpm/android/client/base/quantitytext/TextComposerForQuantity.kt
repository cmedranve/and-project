package pe.com.scotiabank.blpm.android.client.base.quantitytext

import android.view.Gravity
import androidx.annotation.StyleRes
import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class TextComposerForQuantity(
    private val paddingEntity: UiEntityOfPadding,
    @StyleRes private val appearance: Int,
    private val builder: TextBuilderForQuantity,
) : UpdaterOfQuantityText {

    private val textEntity: UiEntityOfText = UiEntityOfText(
        appearance = appearance,
        gravity = Gravity.START,
        text = Constant.EMPTY_STRING,
    )

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfOneColumnText> {

        val entities: List<UiEntityOfOneColumnText> = collect()
        val adapterFactory = AdapterFactoryOfOneColumnText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    private fun collect(): List<UiEntityOfOneColumnText> {

        val entity = UiEntityOfOneColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn = textEntity,
        )

        return listOf(entity)
    }

    override fun updateTextWith(quantity: Int) {
        textEntity.text = builder.build(quantity)
    }
}
