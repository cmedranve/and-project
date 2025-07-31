package pe.com.scotiabank.blpm.android.client.base.session

import androidx.core.util.Supplier

interface SessionHost: Session {

    val platformTypeSupplying: Supplier<String>

    val personTypeSupplying: Supplier<String>

    suspend fun <A : Any> receive(instance: A): Boolean
}
