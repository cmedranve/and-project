package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val idOfCardImage: Long = randomLong(),
    val idOfSaveButton: Long = randomLong(),
    val idOfGoHomeButton: Long = randomLong()
)