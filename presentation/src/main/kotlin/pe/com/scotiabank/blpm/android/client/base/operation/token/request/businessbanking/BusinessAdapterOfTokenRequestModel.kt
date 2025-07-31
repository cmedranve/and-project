package pe.com.scotiabank.blpm.android.client.base.operation.token.request.businessbanking

import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.token.request.AdapterOfTokenRequestModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.businessbanking.BusinessTokenValidationActivity
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.non.NonTokenValidationActivity
import pe.com.scotiabank.blpm.android.client.util.Constant

class BusinessAdapterOfTokenRequestModel(
    dispatcherProvider: DispatcherProvider,
    private val model: TokenRequestModelForBusinessBanking,
): AdapterOfTokenRequestModel, DispatcherProvider by dispatcherProvider {

    override suspend fun adapt(
        canvasConfirmation: CanvasConfirmationEntity
    ): CarrierOfActivityDestination.Builder = withContext(ioDispatcher) {

        val isRequireAuth: Boolean = canvasConfirmation.isShowSmartKey
        if (isRequireAuth.not()) {
            return@withContext CarrierOfActivityDestination.Builder(
                screenDestination = NonTokenValidationActivity::class.java,
            ).putParcelableBy(
                idName = Constant.CANVAS_CONFIRMATION_ENTITY,
                value = canvasConfirmation,
            )
        }

        model.requestToken(canvasConfirmation.transactionId)

        CarrierOfActivityDestination.Builder(
            screenDestination = BusinessTokenValidationActivity::class.java,
        ).putParcelableBy(
            idName = Constant.CANVAS_CONFIRMATION_ENTITY,
            value = canvasConfirmation,
        )
    }
}
