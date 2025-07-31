package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.security.SecurityAuthDataMapper
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.security.request.SecurityAuthWithChallengesRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.security.response.SecurityAuthWithChallengesResponseEntity
import pe.com.scotiabank.blpm.android.data.repository.security.SecurityAuthRepository

class ValidationModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: SecurityAuthRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun validate(
        transactionId: String,
        authId: String,
        challengeType: String,
        challengeValue: String,
    ): SecurityAuthWithChallengesResponseEntity = withContext(ioDispatcher) {

        val requestEntity: SecurityAuthWithChallengesRequestEntity = SecurityAuthDataMapper
            .transformChallengeRequest(
                transactionId = transactionId,
                type = challengeType,
                value = challengeValue,
            )

        val entityOrError: Any = repository
            .validateAuthenticator(
                authId = authId,
                challengesRequest = requestEntity,
            )
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: SecurityAuthWithChallengesResponseEntity = transformToEntity(entityOrError)
        responseEntity
    }
}
