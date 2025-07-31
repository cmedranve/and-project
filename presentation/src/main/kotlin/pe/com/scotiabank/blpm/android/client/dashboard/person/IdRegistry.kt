package pe.com.scotiabank.blpm.android.client.dashboard.person

import com.scotiabank.enhancements.uuid.randomLong

class IdRegistry(
    val coordinatorIdOfDashboard: Long = randomLong(),
    val coordinatorIdOfHome: Long = randomLong(),
    val coordinatorIdOfMyList: Long = randomLong(),
    val coordinatorIdOfP2p: Long = randomLong(),
    val coordinatorIdOfNews: Long = randomLong(),
    val coordinatorIdOfManagementCenter: Long = randomLong(),
)