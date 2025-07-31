package pe.com.scotiabank.blpm.android.client.host.session

import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import pe.com.scotiabank.blpm.android.analytics.factories.newlogin.LoginDashboardFactory
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.network.RetrofitProvider
import pe.com.scotiabank.blpm.android.client.base.network.addAkamaiInterceptor
import pe.com.scotiabank.blpm.android.client.base.network.addChuckerInterceptor
import pe.com.scotiabank.blpm.android.client.base.network.putCertConfiguration
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.host.session.analytics.logindashboard.LoginAnalyticModel
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SuccessfulAuthToLaunch
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.host.user.UserModel
import pe.com.scotiabank.blpm.android.client.host.user.UserModelDelegate
import pe.com.scotiabank.blpm.android.client.nosession.login.factor.SuccessfulAuth
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.DataStore
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.enrollment.refreshtoken.RefreshTokenModel
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.RestApiService
import pe.com.scotiabank.blpm.android.data.net.RestEnrollmentApiService
import pe.com.scotiabank.blpm.android.data.net.interceptor.PeruClientErrorInterceptor
import pe.com.scotiabank.blpm.android.data.net.interceptor.ServerErrorInterceptor
import pe.com.scotiabank.blpm.android.data.net.interceptor.TraceabilityInterceptor
import pe.com.scotiabank.blpm.android.data.net.interceptor.XRelativeInterceptor
import pe.com.scotiabank.blpm.android.data.repository.UserDataRepository
import pe.com.scotiabank.blpm.android.data.repository.enrollment.EnrollmentRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class SessionCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(successfulAuthToLaunch: SuccessfulAuthToLaunch): SessionCoordinator {

        val successfulAuth: SuccessfulAuth = successfulAuthToLaunch.successfulAuth

        val idRegistry = IdRegistry()

        return SessionCoordinator(
            hub = hub,
            weakResources = hub.weakResources,
            weakAppContext = hub.weakAppContext,
            launcher = successfulAuthToLaunch.launcher,
            userModel = createUserModel(successfulAuth),
            appModel = hub.appModel,
            refreshTokenModel = createRefreshTokenModel(),
            idRegistry = idRegistry,
            isQrDeepLink = successfulAuth.isQrDeepLink,
            loginAnalyticModel = createLoginAnalyticModel(),
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = DelegateUiStateHolder(),
        )
    }

    private fun createUserModel(successfulAuth: SuccessfulAuth): UserModel = UserModelDelegate(
        successfulAuth = successfulAuth,
        userDao = hub.userDao,
        cookieProvider = hub.cookieProvider,
        dataStore = DataStore(hub.appContext)
    )

    fun createSessionModel(successfulAuth: SuccessfulAuth, retrofit: Retrofit): SessionModel {

        val api: RestApiService = retrofit.create(RestApiService::class.java)

        return SessionModel(
            dispatcherProvider = hub.dispatcherProvider,
            appModel = hub.appModel,
            userModel = createUserModel(successfulAuth),
            retrofit = retrofit,
            userDataRepository = UserDataRepository(api),
        )
    }

    fun createSessionRetrofit(codeVerifier: CharArray): Retrofit {

        val okHttpClient: OkHttpClient = hub.okHttpClient
            .newBuilder()
            .addInterceptor(
                XRelativeInterceptor(codeVerifier)
            )
            .addAkamaiInterceptor(hub.holderOfSensorData)
            .addInterceptor(
                ServerErrorInterceptor()
            )
            .addInterceptor(
                PeruClientErrorInterceptor(hub.objectMapper)
            )
            .addInterceptor(
                TraceabilityInterceptor()
            )
            .addChuckerInterceptor(hub.appContext)
            .putCertConfiguration(hub.appContext)
            .build()

        return RetrofitProvider.buildRetrofit(
            converterFactory = hub.converterFactoryDecorator,
            url = hub.environment.baseUrlOfApi,
            okHttpClient = okHttpClient,
        )
    }

    private fun createLoginAnalyticModel(): LoginAnalyticModel {

        val factory = LoginDashboardFactory(hub.systemDataFactory)

        return LoginAnalyticModel(
            appModel = hub.appModel,
            analyticsDataGateway = hub.analyticsDataGateway,
            analyticFactory = factory
        )
    }

    private fun createRefreshTokenModel(): RefreshTokenModel {
        val retrofit: Retrofit = hub.appModel.sessionRetrofit
        val api: RestEnrollmentApiService = retrofit.create(RestEnrollmentApiService::class.java)
        val repository = EnrollmentRepository(api, hub.objectMapper)
        return RefreshTokenModel(hub.dispatcherProvider, repository)
    }
}