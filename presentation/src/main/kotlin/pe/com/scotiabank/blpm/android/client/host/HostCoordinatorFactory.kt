package pe.com.scotiabank.blpm.android.client.host

import kotlinx.coroutines.CoroutineScope
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.network.RetrofitProvider
import pe.com.scotiabank.blpm.android.client.base.network.addAkamaiInterceptor
import pe.com.scotiabank.blpm.android.client.base.network.addChuckerInterceptor
import pe.com.scotiabank.blpm.android.client.base.network.putCertConfiguration
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SubFlowLauncher
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.CarrierOfPushData
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.client.util.countdown.ConvenienceCountDownFactory
import pe.com.scotiabank.blpm.android.client.util.countdown.CountDownFactory
import pe.com.scotiabank.blpm.android.data.net.interceptor.PeruClientErrorInterceptor
import pe.com.scotiabank.blpm.android.data.net.interceptor.ServerErrorInterceptor
import retrofit2.Retrofit
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class HostCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(
        featureHubShortcut: FeatureHubShortcut,
        launcher: SubFlowLauncher,
        carrier: CarrierOfPushData,
    ): HostCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()
        val factoryOfMainTopComposite = MainTopComposite.Factory(
            dispatcherProvider = hub.dispatcherProvider,
            uiStateHolder = uiStateHolder,
        )
        val peruRetrofit: Retrofit = createPeruRetrofit()

        return HostCoordinator(
            factoryOfMainTopComposite = factoryOfMainTopComposite,
            hub = hub,
            featureHubShortcut = featureHubShortcut,
            launcher = launcher,
            carrierOfPushData = carrier,
            idRegistry = idRegistry,
            peruRetrofit = peruRetrofit,
            uriHolder = UriHolder(hub.weakResources),
            model = createModel(),
            countDownFactory = createCountDownFactory(),
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createPeruRetrofit(): Retrofit {

        val connectionPool = ConnectionPool(
            maxIdleConnections = 16,
            keepAliveDuration = 5,
            timeUnit = TimeUnit.MINUTES,
        )

        val okHttpClient: OkHttpClient = hub.okHttpClient
            .newBuilder()
            .addAkamaiInterceptor(hub.holderOfSensorData)
            .addInterceptor(
                ServerErrorInterceptor()
            )
            .addInterceptor(
                PeruClientErrorInterceptor(hub.objectMapper)
            )
            .addChuckerInterceptor(hub.appContext)
            .connectionPool(connectionPool)
            .putCertConfiguration(hub.appContext)
            .build()

        return RetrofitProvider.buildRetrofit(
            converterFactory = hub.converterFactoryDecorator,
            url = hub.environment.baseUrlOfApi,
            okHttpClient = okHttpClient,
        )
    }

    private fun createModel(): HostModel = HostModel(
        dispatcherProvider = hub.dispatcherProvider,
        weakAppContext = hub.weakAppContext,
    )

    private fun createCountDownFactory(): CountDownFactory = ConvenienceCountDownFactory(
        total = WAIT_TIME_IN_MILLIS.milliseconds,
        interval = COUNT_DOWN_INTERVAL_MILLIS.milliseconds,
    )

    companion object {

        private val WAIT_TIME_IN_MILLIS: Long
            @JvmStatic
            get() = 1_000L * 60L * 5L

        private val COUNT_DOWN_INTERVAL_MILLIS: Long
            @JvmStatic
            get() = 100L
    }
}