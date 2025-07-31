package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class DataStore(appContext: Context) {

    private val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
        appContext,
    )

    private val edit: SharedPreferences.Editor
        get() = sharedPref.edit()

    val isCvvOnboardingWasShown: Boolean
        get() = sharedPref.getBoolean(DYNAMIC_CVV_ONBOARDING_SHOW, false)

    fun saveCvvOnboardingShown() = edit
        .putBoolean(DYNAMIC_CVV_ONBOARDING_SHOW, true)
        .apply()

    companion object {

        private val DYNAMIC_CVV_ONBOARDING_SHOW: String
            get() = "DYNAMIC_CVV_ONBOARDING_SHOW"
    }

}
