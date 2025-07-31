package pe.com.scotiabank.blpm.android.client.host.session.subflow

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.lang.ref.WeakReference

class EmptySubFlowFactory: SubFlowFactory {

    override fun create(
        hub: Hub,
        parentScope: CoroutineScope,
        weakParent: WeakReference<out Coordinator?>,
    ): Coordinator? = null

    companion object {

        val SHORTCUT_ID: String
            @JvmStatic
            get() = Constant.EMPTY_STRING

        val SHORTCUT_NAME: String
            @JvmStatic
            get() = Constant.EMPTY_STRING
    }
}