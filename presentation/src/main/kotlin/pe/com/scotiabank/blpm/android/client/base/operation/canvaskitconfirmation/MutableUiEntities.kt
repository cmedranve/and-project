package pe.com.scotiabank.blpm.android.client.base.operation.canvaskitconfirmation

import android.graphics.Typeface
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList

class MutableUiData {

    private val mutableDataOfRootView by lazy {
        MutableLayoutDataOfRootView()
    }
    val dataOfRootView: LayoutDataOfRootView
        get() = mutableDataOfRootView

    var staticDataOfBottomSheetList: StaticDataOfBottomSheetList? = null

    fun setUpRootView(singleCcData: DataOfCanvasConfirmation) {
        mutableDataOfRootView.mutableConfirmationData = listOf(singleCcData)
    }
}

private class MutableLayoutDataOfRootView : LayoutDataOfRootView {

    var mutableConfirmationData: List<DataOfCanvasConfirmation> = emptyList()
    override val confirmationData: List<DataOfCanvasConfirmation>
        get() = mutableConfirmationData
}

class MutableDataOfCanvasConfirmation(
    override val canvasConfirmationEntity: CanvasConfirmationEntity,
    override val fontFamily: Typeface,
    override val textColorForWarning: Int
) : DataOfCanvasConfirmation
