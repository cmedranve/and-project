package pe.com.scotiabank.blpm.android.client.base.products.newpicking.bottomsheet

import android.content.res.Resources
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyListType
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.util.recyclerview.DividerPositionUtil
import java.lang.ref.WeakReference

class ProductPickingStaticDataFactory(
    private val titleText: String,
    private val weakResources: WeakReference<Resources?>,
    private val callbackForContinueButton: Runnable,
) {

    private val defaultTitle: String by lazy {
        weakResources.get()?.getString(R.string.choose_origin_account).orEmpty()
    }

    fun create(
        productGroup: ProductGroup,
    ): StaticDataOfBottomSheetList {

        val title: String = titleText.ifBlank { defaultTitle }
        val primaryButtonLabel: String = weakResources.get()?.getString(R.string.btn_continue).orEmpty()
        val dividerPositions: List<Int> = DividerPositionUtil.findDividerPositions(productGroup.selectableProducts)
        val attributes = AttrsBodyListType(title, primaryButtonLabel)

        return StaticDataOfBottomSheetList(
            attributes = attributes,
            dividerPositions = dividerPositions,
            callbackOfPrimaryButton = callbackForContinueButton,
        )
    }
}
