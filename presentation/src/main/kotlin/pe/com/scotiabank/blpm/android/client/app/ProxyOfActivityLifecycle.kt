package pe.com.scotiabank.blpm.android.client.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.scotiabank.enhancements.handling.InstanceReceiver
import java.lang.ref.WeakReference

class ProxyOfActivityLifecycle(
    private val receiverOfActivityLifecycleEvents: InstanceReceiver
) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        forwardToReceiver(EventType.CREATED, activity)
    }

    override fun onActivityStarted(activity: Activity) {
        forwardToReceiver(EventType.STARTED, activity)
    }

    override fun onActivityResumed(activity: Activity) {
        forwardToReceiver(EventType.RESUMED, activity)
    }

    override fun onActivityPaused(activity: Activity) {
        forwardToReceiver(EventType.PAUSED, activity)
    }

    override fun onActivityStopped(activity: Activity) {
        forwardToReceiver(EventType.STOPPED, activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        forwardToReceiver(EventType.SAVE_INSTANCE_STATE, activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        forwardToReceiver(EventType.DESTROYED, activity)
    }

    private fun forwardToReceiver(eventType: EventType, activity: Activity?) {
        if (activity == null) return
        val weakActivity: WeakReference<out Activity?> = WeakReference(activity)
        val activityEvent = ActivityEvent(eventType, weakActivity)
        receiverOfActivityLifecycleEvents.receive(activityEvent)
    }
}
