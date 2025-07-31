package pe.com.scotiabank.blpm.android.client.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.webkit.CookieManager
import androidx.preference.PreferenceManager
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import pe.com.scotiabank.blpm.android.client.base.coroutine.ProviderForCoroutine
import java.lang.ref.WeakReference

class CookieCleaner(
    private val weakAppContext: WeakReference<out Context?>,
    private val storeOfAppPackageInfo: StoreOfAppPackageInfo,
    providerForCoroutine: ProviderForCoroutine,
): ProviderForCoroutine by providerForCoroutine {

    @SuppressLint("ApplySharedPref")
    fun clearCookiesIfFirstInstall() {
        val sharedPref: SharedPreferences = weakAppContext.get()
            ?.let(PreferenceManager::getDefaultSharedPreferences)
            ?: return
        val isCookieCleared: Boolean = sharedPref.getBoolean(IS_COOKIE_CLEARED, false)
        if (isCookieCleared) return
        if (storeOfAppPackageInfo.isFirstInstall().not()) return

        clearSharedCookieStore()
        removeLegacyCookieStore()
        sharedPref.edit().putBoolean(IS_COOKIE_CLEARED, true).commit()
    }

    private fun clearSharedCookieStore() {
        CookieManager.getInstance().removeAllCookies(null)
    }

    private fun removeLegacyCookieStore() {
        val sharedPref: SharedPrefsCookiePersistor = weakAppContext.get()
            ?.let(::SharedPrefsCookiePersistor)
            ?: return
        sharedPref.clear()
        weakAppContext.get()?.deleteSharedPreferences(COOKIE_PERSISTENCE_FILE_NAME)
    }

    companion object {

        private val IS_COOKIE_CLEARED: String
            @JvmStatic
            get() = "IS_COOKIE_CLEARED"

        private val COOKIE_PERSISTENCE_FILE_NAME: String
            @JvmStatic
            get() = "CookiePersistence"
    }
}
