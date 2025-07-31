package pe.com.scotiabank.blpm.android.client.base.checksecurity

import android.content.Context
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.base.checksecurity.CheckSecurityFactory
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import java.lang.ref.WeakReference
import javax.inject.Inject

class FactoryOfCheckSecurityModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val factoryOfChecker: FactoryOfChecker,
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticsFactory: CheckSecurityFactory,
) {

    fun create(
        weakAppContext: WeakReference<out Context?>,
    ): CheckSecurityModel = CheckSecurityModel(
        weakAppContext = weakAppContext,
        dispatcherProvider = dispatcherProvider,
        checker = factoryOfChecker.create(weakAppContext),
        analyticsDataGateway = analyticsDataGateway,
        analyticsFactory = analyticsFactory,
    )
}
