package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.content.res.Resources
import android.text.SpannableStringBuilder
import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.number.DoubleParser
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import java.lang.ref.WeakReference

class CardSettingStore(
    private val weakResources: WeakReference<Resources?>,
    private val settingsFactory: SettingsFactory,
): SettingsByInfoHolder {

    override var settingByInfo: Map<CardSettingInfo, Setting> = emptyMap()

    val settings: Collection<Setting>
        get() = settingByInfo.values

    val isSaveSettingsAllowed: Boolean
        get() = settings.any(::isSettingChanged) && settings.all(::isLimitAllowed)

    val isOtpRequiredForSaving: Boolean
        get() = settings.any(::isOtpRequired)

    private val changedSettings: List<Setting>
        get() = settings.filter(::isSettingChanged)

    private val textForMultipleChanges: String by lazy {
        weakResources.get()?.getString(R.string.card_settings_multiple_changes_snackbar).orEmpty()
    }

    val isTempLockChanged: Boolean
        get() = settings.firstOrNull(::isLockingSetting)?.isSettingChanged == true

    private fun isSettingChanged(setting: Setting): Boolean = setting.isSettingChanged

    private fun isLimitAllowed(setting: Setting): Boolean = setting.isAllowedForAllLimit

    fun createSettingByInfo() {
        settingByInfo = settingsFactory.create()
    }

    fun createSnackbarText(): SpannableStringBuilder {
        val numberOfChangedSettings: Int = changedSettings.size
        val text: String = when (numberOfChangedSettings) {
            0 -> String.EMPTY
            1 -> retrievedTextFromSetting()
            else -> textForMultipleChanges
        }
        return SpannableStringBuilder.valueOf(text)
    }

    private fun retrievedTextFromSetting(): String {
        val setting: Setting = changedSettings.firstOrNull() ?: return String.EMPTY
        @StringRes val textRes: Int = setting.getTextResForSnackbar()
        return weakResources.get()?.getString(textRes).orEmpty()
    }

    private fun isOtpRequired(setting: Setting): Boolean = setting.isOtpRequired
    private fun isLockingSetting(setting: Setting): Boolean = setting.info.cardId == CardSettingInfo.TEMPORARILY_LOCKING.cardId

    class Factory(
        private val weakResources: WeakReference<Resources?>,
        private val card: AtmCardInfo,
        private val infoByCheckingMapper: InfoByCheckingMapper,
        private val holderOfCardSettings: MutableHolderOfCardSettings,
        private val doubleParser: DoubleParser,
    ) {

        fun create(): CardSettingStore = CardSettingStore(
            weakResources = weakResources,
            settingsFactory = createSettingsFactory(),
        )

        private fun createSettingsFactory(): SettingsFactory = SettingsFactory(
            card = card,
            mapper = infoByCheckingMapper,
            factoryOfEditableLimit = createEditableLimitFactory(),
            holderOfCardSettings = holderOfCardSettings,
        )

        private fun createEditableLimitFactory(): FactoryOfEditableLimit = FactoryOfEditableLimit(
            doubleParser = doubleParser,
            weakResources = weakResources,
            holderOfCardSettings = holderOfCardSettings,
        )
    }
}
