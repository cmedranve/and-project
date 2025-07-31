package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.LimitEntity

class Setting(
    val info: CardSettingInfo,
    private val isCheckedFromNetworkCall: Boolean,
    val editableLimits: List<EditableLimit> = emptyList(),
) {

    var isEnabled: Boolean = true
        private set

    var isChecked: Boolean = isCheckedFromNetworkCall
        private set

    var isCheckedForUi: Boolean = isCheckedFromNetworkCall
        private set

    private val isCheckedChanged: Boolean
        get() = if (isEnabled) isCheckedFromNetworkCall != isChecked else false

    private val isLimitsChanged: Boolean
        get() = if (isEnabled) editableLimits.any(::isLimitChanged) else false

    val isSettingChanged: Boolean
        get() = if (isEnabled) isLimitsChanged || isCheckedChanged else false

    val isAllowedForAllLimit: Boolean
        get() = editableLimits.all(::isLimitAllowed)

    val asDataForRequestEntity: Boolean?
        get() = if (isCheckedChanged) isChecked else null

    val limitDataEntities: List<LimitEntity>
        get() = editableLimits.mapNotNull(::attemptToLimitEntity)

    val isOtpRequired: Boolean
        get() = when {
            isSettingChanged.not() -> false
            isCheckedChanged && isChecked -> info.isOtpRequiredForEnabling
            isCheckedChanged && isChecked.not() -> info.isOtpRequiredForDisabling
            else -> false
        }

    fun setIsChecked(isChecked: Boolean) {
        if (isEnabled) {
            this.isChecked = isChecked
            this.isCheckedForUi = isChecked
        }
    }

    private fun isLimitChanged(limit: EditableLimit): Boolean {
        if (isChecked) return limit.isChanged()
        return false
    }

    private fun isLimitAllowed(limit: EditableLimit): Boolean {
        if (isChecked) return limit.isAllowed()
        return true
    }

    private fun attemptToLimitEntity(limit: EditableLimit): LimitEntity? {
        if (isChecked) return limit.asDataEntity
        return null
    }

    @StringRes
    fun getDescriptionResId(type: AtmCardType): Int {
        if (isCheckedForUi) return info.descriptionResIdForEnabling.apply(type)
        return info.descriptionResIdForDisabling.apply(type)
    }

    @StringRes
    fun getTextResForSnackbar(): Int = when {
        isLimitsChanged && isCheckedChanged.not() -> R.string.card_settings_limits_changes_snackbar
        isChecked -> info.snackbarForSaving.textResForEnabling
        else -> info.snackbarForSaving.textResForDisabling
    }

    fun updateSettingAfterLocking(isLocked: Boolean) {
        isEnabled = isLocked.not()
        editableLimits.forEach { limit -> limit.updateLimitAfterLocking(isEnabled = isEnabled) }
        isCheckedForUi = if (isLocked) false else isChecked
    }
}
