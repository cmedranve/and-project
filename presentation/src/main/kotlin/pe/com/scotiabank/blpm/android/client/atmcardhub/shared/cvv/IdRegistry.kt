package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val timerBuddyTipId: Long = randomLong(),
    val showDataButtonId: Long = randomLong(),
    val retryShowDataButtonId: Long = randomLong(),
    val copyButtonId: Long = randomLong(),
)
