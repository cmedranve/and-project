package pe.com.scotiabank.blpm.android.client.assistance

import android.content.Context
import android.content.SharedPreferences
import pe.com.scotiabank.blpm.android.client.assistance.model.BadgeItem
import pe.com.scotiabank.blpm.android.client.assistance.model.StatusBadge
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil

object StatusBadgeHelper {

    private const val PREFS = "status_new"

    @JvmStatic
    fun loadStatusBadge(): StatusBadge {
        val badges: List<BadgeItem> = listOf(
            BadgeItem(TemplatesUtil.SECURITY_KEY, true),
            BadgeItem(TemplatesUtil.INSURANCE_KEY, true),
            BadgeItem(TemplatesUtil.HELP_CENTER_KEY, true),
            BadgeItem(TemplatesUtil.FEEDBACK_MEDALLIA_KEY, true),
            BadgeItem(TemplatesUtil.SECURITY_KEY, true),
            BadgeItem(TemplatesUtil.BIOMETRICS_KEY, true),
            BadgeItem(TemplatesUtil.CONFIGURATION_KEY, true),
            BadgeItem(TemplatesUtil.PUSH_NOTIFICATION, true),
            BadgeItem(TemplatesUtil.PUSH_NOTIFICATION_PAYROLL_PAYMENT, true)
        )

        return StatusBadge(badges)
    }

    @JvmStatic
    fun isViewed(appContext: Context, statusBadgeId: String): Boolean {
        return getAssistancePreferences(appContext)
            .getBoolean(statusBadgeId, false)
    }

    @JvmStatic
    fun saveViewedFinal(appContext: Context, statusBadgeId: String) {
        getAssistancePreferences(appContext)
            .edit()
            .putBoolean(statusBadgeId, true)
            .apply()
    }

    @JvmStatic
    private fun getAssistancePreferences(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun isItemNew(list: List<BadgeItem>?, itemKey: String, appContext: Context): Boolean {
        if (list.isNullOrEmpty()) return false
        for (badgeItem in list) {
            if (isNewItem(badgeItem, itemKey, appContext)) return badgeItem.isNew
        }
        return false
    }

    @JvmStatic
    private fun isNewItem(item: BadgeItem, itemKey: String, appContext: Context): Boolean {
        return itemKey.equals(item.id, ignoreCase = true) && !isViewed(appContext, item.id)
    }

    @JvmStatic
    fun setReadItems(appContext: Context) {
        val statusBadge = loadStatusBadge()
        val list: List<BadgeItem> = statusBadge.badges
        if (list.isEmpty()) return

        for (item in list) {
            if (isViewed(appContext, item.id)) continue
            saveViewedFinal(appContext, item.id)
        }
    }
}
