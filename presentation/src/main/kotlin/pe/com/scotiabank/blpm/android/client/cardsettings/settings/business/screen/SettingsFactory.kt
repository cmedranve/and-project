package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo

class SettingsFactory(
    private val card: AtmCardInfo,
    private val mapper: InfoByCheckingMapper,
    private val factoryOfEditableLimit: FactoryOfEditableLimit,
    private val holderOfCardSettings: MutableHolderOfCardSettings,
) {

    fun create(): Map<CardSettingInfo, Setting> = holderOfCardSettings
        .settingsReceived
        ?.let(mapper::toInfoByChecking)
        ?.mapValues(::toSetting)
        ?: emptyMap()

    private fun toSetting(infoByChecking: Map.Entry<CardSettingInfo, Boolean>): Setting {
        val info: CardSettingInfo = infoByChecking.key
        val isCheckedFromNetworkCall: Boolean = infoByChecking.value
        return Setting(
            info = info,
            isCheckedFromNetworkCall = isCheckedFromNetworkCall,
            editableLimits = toEditableLimits(info),
        )
    }

    private fun toEditableLimits(info: CardSettingInfo): List<EditableLimit> = info
        .getLimitInfo(card.atmCard.type)
        .mapNotNull(factoryOfEditableLimit::attemptToEditableLimit)
}
