package pe.com.scotiabank.blpm.android.client.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import pe.com.scotiabank.blpm.android.client.messaging.notification.PushOtpHandler
import pe.com.scotiabank.blpm.android.client.util.Constant

fun createNotificationChannel(appContext: Context) {

    val channel = NotificationChannel(
        PushOtpHandler.CHANNEL_ID,
        Constant.JOY_NOTIFICATION_CHANNEL,
        NotificationManager.IMPORTANCE_HIGH,
    )

    val systemService: Any = appContext.getSystemService(Context.NOTIFICATION_SERVICE) ?: return
    val notificationManager: NotificationManager = systemService as? NotificationManager ?: return
    notificationManager.createNotificationChannel(channel)
}
