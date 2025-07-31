package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.lang.ref.WeakReference

sealed interface DataOfErrorDialog {

    val title: CharSequence
    val message: CharSequence
    val textForPositiveButton: CharSequence
    val errorCode: Int
}

class DataOfAuthErrorDialog(
    override val title: CharSequence,
    override val message: CharSequence,
    override val textForPositiveButton: CharSequence,
    override val errorCode: Int = Constant.ZERO,
): DataOfErrorDialog

object EmptyDataOfErrorDialog : DataOfErrorDialog {

    override val title: CharSequence
        get() = Constant.EMPTY_STRING
    override val message: CharSequence
        get() = Constant.EMPTY_STRING
    override val errorCode: Int
        get() = Constant.ZERO
    override val textForPositiveButton: CharSequence
        get() = Constant.EMPTY_STRING
}

class HelperForAuthErrorDialog(private val weakResources: WeakReference<Resources?>) {

    val title: CharSequence by lazy {
        weakResources.get()
            ?.getString(R.string.biometric_title)
            .orEmpty()
    }

    val textForPositiveButton: CharSequence by lazy {
        weakResources.get()
            ?.getString(R.string.accept)
            .orEmpty()
    }
}
