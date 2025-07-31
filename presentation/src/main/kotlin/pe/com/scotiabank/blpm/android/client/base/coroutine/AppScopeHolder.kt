package pe.com.scotiabank.blpm.android.client.base.coroutine

import kotlinx.coroutines.CoroutineScope

interface AppScopeHolder {

    val appScope: CoroutineScope
}
