package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.informativebottomsheet

import android.content.res.Resources
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyListType
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.lang.ref.WeakReference

class CvvInformativeStaticDataHolder(
    private val weakResources: WeakReference<Resources?>,
    private val callback: Runnable? = null,
) {

    val data: StaticDataOfBottomSheetList by lazy {
        create()
    }

    private fun create(): StaticDataOfBottomSheetList {

        val title: String = weakResources.get()?.getString(R.string.dynamic_cvv).orEmpty()

        val buttonLabel: String = weakResources.get()?.getString(R.string.go_to_back).orEmpty()

        val attributes = AttrsBodyListType(title, buttonLabel, Constant.EMPTY_STRING)

        return StaticDataOfBottomSheetList(
            attributes = attributes,
            isCancelable = false,
            callbackOfPrimaryButton = callback,
        )
    }

}
