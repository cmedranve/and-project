package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val installmentFieldId: Long = randomLong(),
    val twoColumnIdOfExchangeRate: Long = randomLong(),
    val continueButtonId: Long = randomLong(),
)
