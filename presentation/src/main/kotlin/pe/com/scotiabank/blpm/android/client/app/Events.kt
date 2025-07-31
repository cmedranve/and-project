package pe.com.scotiabank.blpm.android.client.app

import android.app.Activity
import java.lang.ref.WeakReference

enum class EventType(val typeId: Int) {
    CREATED(0),
    STARTED(1),
    RESUMED(2),
    PAUSED(3),
    STOPPED(4),
    SAVE_INSTANCE_STATE(5),
    DESTROYED(6)
}

class ActivityEvent(private val eventType: EventType, val weakActivity: WeakReference<out Activity?>) {

    companion object {

        @JvmStatic
        fun filterOnCreated(activityEvent: ActivityEvent): Boolean {
            return EventType.CREATED == activityEvent.eventType
        }

        @JvmStatic
        fun filterOnStarted(activityEvent: ActivityEvent): Boolean {
            return EventType.STARTED == activityEvent.eventType
        }

        @JvmStatic
        fun filterOnResumed(activityEvent: ActivityEvent): Boolean {
            return EventType.RESUMED == activityEvent.eventType
        }

        @JvmStatic
        fun filterOnPaused(activityEvent: ActivityEvent): Boolean {
            return EventType.PAUSED == activityEvent.eventType
        }

        @JvmStatic
        fun filterOnStopped(activityEvent: ActivityEvent): Boolean {
            return EventType.STOPPED == activityEvent.eventType
        }

        @JvmStatic
        fun filterOnSaveInstanceState(activityEvent: ActivityEvent): Boolean {
            return EventType.SAVE_INSTANCE_STATE == activityEvent.eventType
        }

        @JvmStatic
        fun filterOnDestroyed(activityEvent: ActivityEvent): Boolean {
            return EventType.DESTROYED == activityEvent.eventType
        }
    }
}
