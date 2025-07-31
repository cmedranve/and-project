package pe.com.scotiabank.blpm.android.client.host.session.subflow

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import java.lang.ref.WeakReference

interface SubFlowFactory {

    fun create(
        hub: Hub,
        parentScope: CoroutineScope,
        weakParent: WeakReference<out Coordinator?>,
    ): Coordinator?
}