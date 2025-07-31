package pe.com.scotiabank.blpm.android.client.base.operation.canvaskitconfirmation

import android.graphics.Typeface
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity

interface LayoutDataOfRootView {

    val confirmationData: List<DataOfCanvasConfirmation>
}

interface DataOfCanvasConfirmation {

    val canvasConfirmationEntity: CanvasConfirmationEntity
    val fontFamily: Typeface
    val textColorForWarning: Int
}
