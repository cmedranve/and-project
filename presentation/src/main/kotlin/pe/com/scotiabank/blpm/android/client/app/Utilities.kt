package pe.com.scotiabank.blpm.android.client.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import com.google.firebase.analytics.FirebaseAnalytics
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.sdk.crasherrorreporting.ThrowableReporter
import com.scotiabank.sdk.crasherrorreporting.UncaughtExceptionHandlerDecorator
import pe.com.scotiabank.blpm.android.client.base.BaseActivity
import java.lang.ref.WeakReference

fun initializeFirebaseAnalytics(appContext: Context) {
    FirebaseAnalytics.getInstance(appContext).setAnalyticsCollectionEnabled(true)
}

fun setUpUncaughtExceptionHandler(throwableReporter: ThrowableReporter) {
    val thread = Thread.currentThread()
    val uncaughtExceptionHandler: Thread.UncaughtExceptionHandler = thread.uncaughtExceptionHandler ?: return
    thread.uncaughtExceptionHandler = UncaughtExceptionHandlerDecorator(throwableReporter, uncaughtExceptionHandler)
}

fun registerActivityLifecycleCallbacks(
    receiverOfActivityLifecycleEvents: InstanceReceiver,
    weakApp: WeakReference<out Application?>
) {
    val lifecycleCallbacks: Application.ActivityLifecycleCallbacks = ProxyOfActivityLifecycle(
        receiverOfActivityLifecycleEvents = receiverOfActivityLifecycleEvents
    )
    weakApp.get()?.registerActivityLifecycleCallbacks(lifecycleCallbacks)
}

fun disableOverviewScreenshot(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        activity.setRecentsScreenshotEnabled(false)
    }
}

fun shouldExpireSession(activity: Activity): Boolean {
    return activity is BaseActivity && activity.shouldExpireSession()
}
