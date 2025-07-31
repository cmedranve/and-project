package pe.com.scotiabank.blpm.android.client.base.network

import android.content.Context
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import pe.com.scotiabank.blpm.android.data.net.interceptor.ProxyOfErrorInterceptor

interface FactoryOfOkHttpClient {

    fun create(
        appContext: Context,
        proxyOfErrorInterceptor: ProxyOfErrorInterceptor,
        cookieJar: CookieJar,
    ): OkHttpClient
}
