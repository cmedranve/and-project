package pe.com.scotiabank.blpm.android.client.host.shared

import kotlinx.coroutines.flow.MutableSharedFlow

interface HolderOfInstanceFlow {

    val instanceFlow: MutableSharedFlow<Any>
}