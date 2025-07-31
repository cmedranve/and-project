package pe.com.scotiabank.blpm.android.client.host.user

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import pe.com.scotiabank.blpm.android.client.nosession.login.factor.SuccessfulAuth
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.DataStore
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieProvider
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieRemoverFromMemory
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieSupplier
import pe.com.scotiabank.blpm.android.data.repository.nonsession.LogoutRepository

class UserModelDelegate(
    private val successfulAuth: SuccessfulAuth,
    private val userDao: UserDao,
    private val cookieProvider: CookieProvider,
    private val dataStore: DataStore,
): UserModel, CookieSupplier by cookieProvider, CookieRemoverFromMemory by cookieProvider {

    private val authenticatedUser: User
        get() = successfulAuth.user

    private val retrievedUserFromStore: User
        get() = userDao.user

    private val isAuthenticatedUserFromStore: Boolean
        get() = authenticatedUser.userId.contentEquals(retrievedUserFromStore.userId)

    private val isUserFromStoreDistrustingTheirDevice: Boolean
        get() = isAuthenticatedUserFromStore && successfulAuth.trustedDevice.not()

    private val logoutRepository: LogoutRepository
        get() = successfulAuth.logoutRepository

    override fun saveChangesIf(
        nickName: CharArray,
        avatar: CharArray,
        isQrDeepLinkAvailable: Boolean,
        isContactPayQrAvailable: Boolean,
    ) = when {
        dataStore.isEnrolledDevice -> saveUser(nickName, avatar, isQrDeepLinkAvailable, isContactPayQrAvailable)
        isUserFromStoreDistrustingTheirDevice -> removeUser()
        successfulAuth.trustedDevice -> saveUser(nickName, avatar, isQrDeepLinkAvailable, isContactPayQrAvailable)
        else -> Unit
    }

    private fun removeUser() {
        cookieProvider.removeCookiesFromDisk()
        userDao.clear()
    }

    private fun saveUser(
        nickName: CharArray,
        avatar: CharArray,
        isQrDeepLinkAvailable: Boolean,
        isContactPayQrAvailable: Boolean,
    ) {
        val user = User(
            isQrDeepLinkAvailable = isQrDeepLinkAvailable,
            isContactPayQrAvailable = isContactPayQrAvailable,
            userId = authenticatedUser.userId,
            nickName = nickName,
            avatar = avatar,
        )
        userDao.saveUser(user)
    }

    override suspend fun logout() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        tryLoggingOut()
        removeCookiesFromMemory()
    }

    private suspend fun tryLoggingOut() = try {
        logoutRepository.logout(successfulAuth.codeVerifier)
    } catch (throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    override fun removeUserIf() {
        cookieProvider.removeCookiesFromMemory()
        if (isAuthenticatedUserFromStore.not()) return
        removeUser()
    }
}
