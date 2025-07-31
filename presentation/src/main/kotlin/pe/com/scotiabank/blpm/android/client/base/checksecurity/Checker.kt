package pe.com.scotiabank.blpm.android.client.base.checksecurity

interface Checker {

    suspend fun isHooked(): Boolean

    suspend fun isTampered(): Boolean

    suspend fun isEmulator(): Boolean

    fun isRootedDevice(): Boolean
}
