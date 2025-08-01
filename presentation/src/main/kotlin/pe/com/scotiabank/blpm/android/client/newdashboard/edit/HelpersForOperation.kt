package pe.com.scotiabank.blpm.android.client.newdashboard.edit

import android.content.res.Resources
import android.text.InputFilter
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.operation.IdentifiableEditText
import java.lang.ref.WeakReference

val LENGTH_OF_OPERATION_NAME
    get() = 30

interface HelperForOperation {

    val titleText: String
    val hintText: CharSequence
    val filters: Array<InputFilter>
    val data: IdentifiableEditText
}

class HelperForCustomerOperation(
    weakResources: WeakReference<Resources?>,
    maxLength: Int = LENGTH_OF_OPERATION_NAME,
) : HelperForOperation {

    override val titleText: String = weakResources.get()
        ?.getString(R.string.my_list_edit_name_tranfer)
        .orEmpty()

    override val hintText: CharSequence = weakResources.get()
        ?.getString(R.string.my_list_edit_name_tranfer_hint)
        .orEmpty()

    override val filters: Array<InputFilter> = arrayOf(
        InputFilter.LengthFilter(maxLength)
    )

    override val data: IdentifiableEditText = IdentifiableEditText.CUSTOMER_PRODUCT_NUMBER
}