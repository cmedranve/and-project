package pe.com.scotiabank.blpm.android.client.host.user

import pe.com.scotiabank.blpm.android.data.net.cookie.CookieRemoverFromMemory
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieSupplier

interface UserModel : CookieSupplier, CookieRemoverFromMemory {

    fun saveChangesIf(nickName: CharArray, avatar: CharArray, isQrDeepLinkAvailable: Boolean, isContactPayQrAvailable: Boolean)

    suspend fun logout()

    fun removeUserIf()
}