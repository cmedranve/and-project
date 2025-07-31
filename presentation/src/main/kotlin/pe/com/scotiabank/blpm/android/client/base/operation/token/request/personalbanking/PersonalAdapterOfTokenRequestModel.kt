package pe.com.scotiabank.blpm.android.client.base.operation.token.request.personalbanking

import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.token.request.AdapterOfTokenRequestModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.request.SecurityAuthenticator
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.non.NonTokenValidationActivity
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking.PersonalTokenValidationActivity
import pe.com.scotiabank.blpm.android.client.model.security.SecurityAuthModel
import pe.com.scotiabank.blpm.android.client.util.Constant

class PersonalAdapterOfTokenRequestModel(
    dispatcherProvider: DispatcherProvider,
    private val model: TokenRequestModelForPersonalBanking,
    private val securityAuthenticator: SecurityAuthenticator,
): AdapterOfTokenRequestModel, DispatcherProvider by dispatcherProvider {

    override suspend fun adapt(
        canvasConfirmation: CanvasConfirmationEntity,
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

        val securityAuth: SecurityAuthModel = model.requestToken(
            transactionId = canvasConfirmation.transactionId,
            type = securityAuthenticator.type,
            deviceId = securityAuthenticator.deviceId,
        )

        CarrierOfActivityDestination.Builder(
            screenDestination = PersonalTokenValidationActivity::class.java,
        ).putParcelableBy(
            idName = Constant.CANVAS_CONFIRMATION_ENTITY,
            value = canvasConfirmation,
        ).putParcelableBy(
            idName = Constant.SECURITY_AUTH,
            value =  securityAuth,
        )
    }
}
