package pe.com.scotiabank.blpm.android.client.host.shared

import androidx.fragment.app.FragmentActivity
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.weakreference.getEmptyWeak
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class UserInterface(
    private val scope: CoroutineScope,
): InstanceReceiver, HolderOfWeakActivity, HolderOfInstanceFlow {

    override var weakActivity: WeakReference<FragmentActivity?> = getEmptyWeak()

    override val instanceFlow: MutableSharedFlow<Any> = MutableSharedFlow(replay = Int.MAX_VALUE)

    override fun <A : Any> receive(instance: A): Boolean {
        scope.launch {
            instanceFlow.emit(instance)
        }
        return false
    }
}