package pe.com.scotiabank.blpm.android.client.base.operation.token.request.personalbanking

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.model.security.SecurityAuthModel
import pe.com.scotiabank.blpm.android.client.security.SecurityAuthDataMapper
import pe.com.scotiabank.blpm.android.client.security.SecurityAuthDataMapper.toSecurityAuthModel
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.security.request.SecurityAuthWithUserRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.security.response.SecurityAuthWithUserResponseEntity
import pe.com.scotiabank.blpm.android.data.repository.security.SecurityAuthRepository

class TokenRequestModelForPersonalBanking(
    dispatcherProvider: DispatcherProvider,
    private val repository: SecurityAuthRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun requestToken(
        transactionId: String,
        type: String,
        deviceId: String,
    ): SecurityAuthModel = withContext(ioDispatcher) {

        val requestEntity: SecurityAuthWithUserRequestEntity = SecurityAuthDataMapper.transformUserRequest(
            transactionId = transactionId,
            type = type,
            deviceId = deviceId,
        )

        val entityOrError: Any = repository.requestAuthenticator(requestEntity)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: SecurityAuthWithUserResponseEntity = transformToEntity(entityOrError)
        responseEntity.toSecurityAuthModel()
    }
}
