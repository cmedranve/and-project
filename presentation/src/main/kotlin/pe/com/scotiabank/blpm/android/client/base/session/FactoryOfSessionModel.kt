package pe.com.scotiabank.blpm.android.client.base.session

import pe.com.scotiabank.blpm.android.analytics.AnalyticsUserGateway
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.newdashboard.BusinessDashboardModel
import pe.com.scotiabank.blpm.android.client.newdashboard.PersonalDashboardModel
import pe.com.scotiabank.blpm.android.client.security.NotificationSharedPreferences
import javax.inject.Inject

class FactoryOfSessionModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val environmentHolder: EnvironmentHolder,
    private val notificationSharedPreferences: NotificationSharedPreferences,
    private val analyticsUserGateway: AnalyticsUserGateway,
) {

    fun create(): SessionModel = SessionModel(
        dispatcherProvider = dispatcherProvider,
        environmentHolder = environmentHolder,
        modelFactoryForBusinessDashboard = createModelFactoryForBusinessDashboard(),
        modelFactoryForPersonalDashboard = createModelFactoryForPersonalDashboard(),
    )

    private fun createModelFactoryForBusinessDashboard() = BusinessDashboardModel.Factory(
        analyticsUserGateway = analyticsUserGateway,
    )

    private fun createModelFactoryForPersonalDashboard() = PersonalDashboardModel.Factory(
        notificationSharedPreferences = notificationSharedPreferences,
        analyticsUserGateway = analyticsUserGateway,
    )
}
