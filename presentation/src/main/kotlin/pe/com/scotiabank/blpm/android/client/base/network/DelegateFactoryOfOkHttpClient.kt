package pe.com.scotiabank.blpm.android.client.base.network

import android.content.Context
import okhttp3.*
import pe.com.scotiabank.blpm.android.client.BuildConfig
import pe.com.scotiabank.blpm.android.data.net.interceptor.ProxyOfErrorInterceptor
import java.util.concurrent.TimeUnit

class DelegateFactoryOfOkHttpClient(private val dispatcher: Dispatcher) : FactoryOfOkHttpClient {

    override fun create(
        appContext: Context,
        proxyOfErrorInterceptor: ProxyOfErrorInterceptor,
        cookieJar: CookieJar,
    ): OkHttpClient {

        val connectionSpecs: List<ConnectionSpec> = ConnectionSpecFactory.createSpecs()

        return OkHttpClient.Builder()
            .addInterceptor(
                LoggingFactory.create()
            )
            .addInterceptor(proxyOfErrorInterceptor)
            .addUserAgentInterceptor(appContext)
            .cache(null)
            .cookieJar(cookieJar)
            .connectionPool(ConnectionPool())
            .connectionSpecs(connectionSpecs)
            .dispatcher(dispatcher)
            .followRedirects(true)
            .putCertConfiguration(appContext)
            .followSslRedirects(true)
            .connectTimeout(BuildConfig.TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

}
