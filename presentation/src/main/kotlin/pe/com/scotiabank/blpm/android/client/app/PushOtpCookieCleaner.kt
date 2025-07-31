package pe.com.scotiabank.blpm.android.client.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import pe.com.scotiabank.blpm.android.client.base.coroutine.ProviderForCoroutine
import pe.com.scotiabank.blpm.android.client.host.user.UserDao
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieProvider
import java.lang.ref.WeakReference

class PushOtpCookieCleaner(
    private val weakAppContext: WeakReference<out Context?>,
    private val storeOfAppPackageInfo: StoreOfAppPackageInfo,
    private val cookieProvider: CookieProvider,
    private val userDao: UserDao,
    providerForCoroutine: ProviderForCoroutine,
): ProviderForCoroutine by providerForCoroutine {

    @SuppressLint("ApplySharedPref")
    fun clearCookiesIfAppUpdated() {
        val sharedPref: SharedPreferences = weakAppContext.get()
            ?.let(PreferenceManager::getDefaultSharedPreferences)
            ?: return

        if (isPushOtpCookieCleared(sharedPref) || storeOfAppPackageInfo.isFirstInstall()) return

        removeCookies()
        removeUser()
        savePushOtpCookieCleared(sharedPref)

    }

    private fun isPushOtpCookieCleared(
        sharedPref: SharedPreferences
    ): Boolean = sharedPref.getBoolean(IS_PUSH_OTP_COOKIE_CLEARED, false)

    private fun savePushOtpCookieCleared(
        sharedPref: SharedPreferences
    ) = sharedPref.edit().putBoolean(IS_PUSH_OTP_COOKIE_CLEARED, true).commit()

    private fun removeCookies() = with(cookieProvider) {
        removeCookiesFromMemory()
        removeCookiesFromDisk()
    }

    private fun removeUser() {
        userDao.clear()
    }

    companion object {

        private val IS_PUSH_OTP_COOKIE_CLEARED: String
            @JvmStatic
            get() = "IS_PUSH_OTP_COOKIE_CLEARED"
    }
}
