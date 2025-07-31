package pe.com.scotiabank.blpm.android.client.base.operation.currencyamount

import android.content.res.Resources
import android.text.InputFilter
import android.text.InputType
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.operation.IdentifiableEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.CollectorOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.DecimalDigitsInputFilter
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import java.lang.ref.WeakReference
import java.util.*

class CollectorOfEditableAmount(
    defaultLocale: Locale,
    private val weakResources: WeakReference<Resources?>,
): CollectorOfEditText<IdentifiableEditText> {

    private val filters: Array<InputFilter> = arrayOf(
        DecimalDigitsInputFilter(locale = defaultLocale)
    )

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver,
        toolTipEntity: UiEntityOfToolTip?,
    ): List<UiEntityOfEditText<IdentifiableEditText>> {

        val amountEntity = UiEntityOfEditText(
            paddingEntity = paddingEntity,
            titleText = weakResources.get()?.getString(R.string.amount).orEmpty(),
            hintText = weakResources.get()?.getString(R.string.fx_quotation_zero_amount).orEmpty(),
            receiver = receiver,
            filters = filters,
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            data = IdentifiableEditText.AMOUNT,
        )

        return listOf(amountEntity)
    }
}
