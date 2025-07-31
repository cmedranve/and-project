package pe.com.scotiabank.blpm.android.client.base.verification

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val idOfOtpInput: Long = randomLong(),
    val idOfGroup: Long = randomLong(),
    val idOfRetrying: Long = randomLong(),
    val idOfSendingBy: Long = randomLong(),
    val idOfContinueButton: Long = randomLong(),
)
