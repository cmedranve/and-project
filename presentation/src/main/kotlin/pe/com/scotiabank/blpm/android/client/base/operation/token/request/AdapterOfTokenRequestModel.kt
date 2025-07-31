package pe.com.scotiabank.blpm.android.client.base.operation.token.request

import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination

interface AdapterOfTokenRequestModel {

    suspend fun adapt(
        canvasConfirmation: CanvasConfirmationEntity,
    ): CarrierOfActivityDestination.Builder
}
