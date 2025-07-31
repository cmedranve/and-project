package pe.com.scotiabank.blpm.android.client.base.network

import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieServiceFromDisk
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieServiceFromMemory
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieRepository

object FactoryOfCookieJar {

    @JvmStatic
    fun createCookieJar(
        cookieRepository: CookieRepository,
    ): CookieJar = JavaNetCookieJar(cookieHandler = cookieRepository)

    @JvmStatic
    fun createCookieRepository(): CookieRepository {
        val memory = CookieServiceFromMemory(
            adaptee = java.net.CookieManager(),
        )
        val disk = CookieServiceFromDisk(
            adaptee = android.webkit.CookieManager.getInstance(),
        )
        return CookieRepository(memory = memory, disk = disk)
    }
}
