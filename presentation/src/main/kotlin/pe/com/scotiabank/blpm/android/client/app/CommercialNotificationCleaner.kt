package pe.com.scotiabank.blpm.android.client.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import pe.com.scotiabank.blpm.android.client.base.coroutine.ProviderForCoroutine
import pe.com.scotiabank.blpm.android.client.messaging.NotificationSetting
import pe.com.scotiabank.blpm.android.client.security.NOTIFICATION_PREFS
import java.lang.ref.WeakReference

class CommercialNotificationCleaner(
    private val weakAppContext: WeakReference<out Context?>,
    providerForCoroutine: ProviderForCoroutine,
): ProviderForCoroutine by providerForCoroutine {

    private val sharedPref: SharedPreferences?
        get() = weakAppContext.get()
            ?.let(::createSharedPreferences)

    private fun createSharedPreferences(
        appContext: Context,
    ): SharedPreferences = appContext.getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE)

    @SuppressLint("ApplySharedPref")
    fun clearLegacyNotificationFlags() {
        val sharedPreferences: SharedPreferences = sharedPref ?: return
        val legacyNames: List<String> = sharedPreferences
            .all
            .filter(::isLegacyEntry)
            .map(::toName)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        legacyNames.forEach { name -> editor.remove(name) }
        editor.commit()
    }

    private fun isLegacyEntry(entry: Map.Entry<String, Any?>): Boolean {
        val name: String = entry.key
        return name.endsWith(NotificationSetting.NEW_USER)
                || name.endsWith(NotificationSetting.OTHER_USER)
                || name.endsWith(NotificationSetting.LINKED_USER)
    }

    private fun toName(entry: Map.Entry<String, Any?>): String = entry.key
}
