package pe.com.scotiabank.blpm.android.client.assistance

import android.content.Context
import android.content.SharedPreferences
import pe.com.scotiabank.blpm.android.client.assistance.model.*
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.newdashboard.NewDashboardActivity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.ContextualAssistanceConstant

object ContextualAssistanceHelper {

    private const val PREFS = "assistance"

    @JvmStatic
    fun loadContextualAssistance(): ContextualAssistance {
        val items: List<AssistanceItem> = listOf(
            CoachMarkItem(
                title = "Quiero",
                description = "Desde esta pestaña puedes pagar, transferir y hacer otras operaciones.",
                actionUri = Constant.EMPTY_STRING,
                buttonType = 1,
                viewId = "tab_id_want",
                id = "WANT_NEW_R11",
                displayTimes = 1,
                type = "first_login",
                isNewUser = true,
                profileList = listOf(Constant.PROFILE_TYPE_FULL, Constant.PROFILE_TYPE_RESTRICTED)
            ),
            CoachMarkItem(
                title = "Plin en Contactos",
                description = "Usa Plin desde Contactos para enviar dinero gratis y de inmediato.",
                actionUri = Constant.EMPTY_STRING,
                buttonType = 1,
                viewId = "navigation_contacts",
                id = "LOOP_2_PAY_OPTIONAL_R11",
                displayTimes = 1,
                type = "first_login",
                isNewUser = true,
                profileList = listOf(Constant.PROFILE_TYPE_FULL)
            ),
            CoachMarkItem(
                title = "Pagar con QR",
                description = "Paga de forma segura y rápida escaneando los códigos QR.",
                actionUri = Constant.EMPTY_STRING,
                buttonType = 0,
                viewId = "qr_camera_settings",
                id = "QR_NEW_R10_5",
                displayTimes = 1,
                type = "second_login",
                isNewUser = true,
                profileList = listOf(Constant.PROFILE_TYPE_FULL)
            ),
            CoachMarkItem(
                title = "Mi Cuenta",
                description = "Desde esta pestaña puedes configurar los límites de tus tarjetas, activarla cuando viajes y otras opciones.",
                actionUri = Constant.EMPTY_STRING,
                buttonType = 2,
                viewId = "navigation_profile",
                id = "MORE_NEW_R11",
                displayTimes = 1,
                type = "first_login",
                isNewUser = true,
                profileList = listOf(Constant.PROFILE_TYPE_FULL, Constant.PROFILE_TYPE_RESTRICTED)
            ),
            FullScreenItem(
                id = "L2P_R17",
                displayTimes = 1,
                type = "third_login",
                isNewUser = true,
                profileList = listOf(Constant.PROFILE_TYPE_FULL)
            ),
            FullScreenItem(
                id = "L2P_R17",
                displayTimes = 1,
                type = "second_login",
                isNewUser = false,
                profileList = listOf(Constant.PROFILE_TYPE_FULL)
            )
        )
        val screen = Screen(NewDashboardActivity.SCREEN_TAG, items)
        val screens: List<Screen> = listOf(screen)
        return ContextualAssistance(screens)
    }

    @JvmStatic
    fun isAssistanceItemListViewed(appContext: Context, list: List<AssistanceItem>): Boolean {
        for (item in list) {
            if (!isViewed(appContext, item.id)) return false
        }
        return true
    }

    // FIXME: Needs revision to improve the filter and this can support showing components of the following session, in case there are components turned off by feature flag
    @JvmStatic
    fun filterContextualAssistance(
        appModel: AppModel,
        appContext: Context,
        items: List<AssistanceItem>,
    ): List<AssistanceItem> {
        val filteredItems: MutableList<AssistanceItem> = mutableListOf()
        val newUser: Boolean = appModel.isFirstInstall()
        var loginAttempts = Constant.EMPTY_STRING
        for (item in items) {
            val isViewed = isViewed(appContext, item.id)
            if (item.isNewUser == newUser && item.profileList.contains(appModel.profile.profileType) && !isViewed) {
                if (loginAttempts == Constant.EMPTY_STRING) {
                    filteredItems.add(item)
                    loginAttempts = item.type
                } else if (loginAttempts == item.type) {
                    filteredItems.add(item)
                }
            }
        }
        return filteredItems
    }

    @JvmStatic
    fun isViewed(appContext: Context, assistanceItemId: String): Boolean {
        if (ContextualAssistanceConstant.WHATS_NEW == assistanceItemId) return false
        return getAssistancePreferences(appContext).getBoolean(assistanceItemId, false)
    }

    @JvmStatic
    fun saveViewedFinal(appContext: Context, assistanceItemId: String?) {
        getAssistancePreferences(appContext)
            .edit()
            .putBoolean(assistanceItemId, true)
            .apply()
    }

    @JvmStatic
    private fun getAssistancePreferences(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }
}
