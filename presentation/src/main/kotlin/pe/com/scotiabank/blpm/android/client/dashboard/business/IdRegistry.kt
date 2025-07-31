package pe.com.scotiabank.blpm.android.client.dashboard.business

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val coordinatorIdOfDashboard: Long = randomLong(),
    val coordinatorIdOfHome: Long = randomLong(),
    val coordinatorIdOfScotiaPay: Long = randomLong(),
    val coordinatorIdOfManagementCenter: Long = randomLong(),
)