package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

interface CardService {

    fun addCard(setting: Setting)

    fun editCard(switchId: Long, isChecked: Boolean)

    fun updateCardsAfterLocking(isLocked: Boolean)

    fun clearCards()
}