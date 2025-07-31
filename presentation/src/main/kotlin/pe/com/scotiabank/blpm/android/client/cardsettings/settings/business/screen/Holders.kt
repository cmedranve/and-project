package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.CardSettings

interface HolderOfCardSettings {

    val settingsReceived: CardSettings?
}

class MutableHolderOfCardSettings : HolderOfCardSettings {

    override var settingsReceived: CardSettings? = null
}

interface SettingsByInfoHolder {

    val settingByInfo: Map<CardSettingInfo, Setting>
}