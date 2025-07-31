package pe.com.scotiabank.blpm.android.client.host

import android.content.Context
import com.google.android.gms.security.ProviderInstaller
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import java.lang.ref.WeakReference

class HostModel(
    dispatcherProvider: DispatcherProvider,
    private val weakAppContext: WeakReference<Context?>,
): DispatcherProvider by dispatcherProvider {

    suspend fun isGooglePlayAvailable(): Boolean = withContext(defaultDispatcher) {
        weakAppContext.get()?.let(::isEnabledAndUpToDate) ?: false
    }

    private fun isEnabledAndUpToDate(appContext: Context): Boolean {
        ProviderInstaller.installIfNeeded(appContext)
        return true
    }
}