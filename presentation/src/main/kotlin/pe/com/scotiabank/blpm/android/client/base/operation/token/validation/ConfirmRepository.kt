package pe.com.scotiabank.blpm.android.client.base.operation.token.validation

import io.reactivex.Observable
import pe.com.scotiabank.blpm.android.data.entity.ReferenceRequestEntity

fun interface ConfirmRepository<T : Any> {

    fun confirmOperation(
        transactionId: String,
        authTracking: String,
        authToken: String,
        referenceRequestEntity: ReferenceRequestEntity,
    ): Observable<T?>
}
