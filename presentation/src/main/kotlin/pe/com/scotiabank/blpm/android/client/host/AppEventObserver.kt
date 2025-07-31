package pe.com.scotiabank.blpm.android.client.host

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.newwebview.EmptyPortableViewModel
import java.lang.ref.WeakReference

class AppEventObserver(
    private val appModel: AppModel,
    private val weakCoordinator: WeakReference<out Coordinator?>,
    override val id: Long = randomLong(),
) : EmptyPortableViewModel {

    fun start() {
        appModel.addChild(this)
    }

    override fun receiveEvent(event: Any): Boolean = weakCoordinator.get()
        ?.currentDeepChild
        ?.receiveEvent(event)
        ?: false

    fun reset() {
        stop()
        start()
    }

    fun stop() {
        appModel.removeChild(id)
    }
}