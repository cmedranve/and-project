package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val tabIdOfSummary: Long = randomLong(),
    val buttonIdOfSummaryRetry: Long = randomLong(),
    val addButtonId: Long = randomLong(),
    val amountButtonId: Long = randomLong(),
)