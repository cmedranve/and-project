package pe.com.scotiabank.blpm.android.client.base.checksecurity

import android.content.Context
import android.os.Build
import com.guardsquare.dexguard.runtime.detection.CertificateChecker
import com.guardsquare.dexguard.runtime.detection.FileChecker
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.*
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket

class CheckerDelegate(
    dispatcherProvider: DispatcherProvider,
    private val weakAppContext: WeakReference<out Context?>,
): Checker, DispatcherProvider by dispatcherProvider {

    override suspend fun isHooked(): Boolean = withContext(ioDispatcher) {
        val socket = Socket()
        val localAddress = InetSocketAddress(LOCALHOST, DEFAULT_PORT)
        isConnectedToLocalHost(socket, localAddress)
    }

    private fun isConnectedToLocalHost(socket: Socket, localAddress: InetSocketAddress): Boolean = try {
        socket.use { closeableSocket -> closeableSocket.connect(localAddress) }
        true
    } catch (e: Exception) {
        false
    }

    override suspend fun isTampered(): Boolean = withContext(defaultDispatcher) {

        val certResult: Int = weakAppContext.get()?.let(::isCertChanged) ?: return@withContext false
        val fileResult: Int = weakAppContext.get()?.let(::isAnyFileChanged) ?: return@withContext false
        certResult != CERT_OK || fileResult != ALL_FILES_OK
    }

    private fun isCertChanged(appContext: Context): Int = CertificateChecker.checkCertificate(
        appContext,
        certSha256,
        CERT_OK,
    )

    private fun isAnyFileChanged(appContext: Context): Int {
        val fileChecker = FileChecker(appContext)
        return fileChecker.checkAllFiles(ALL_FILES_OK)
    }

    override suspend fun isEmulator(): Boolean = withContext(defaultDispatcher) {
        (Build.BRAND.lowercase().startsWith(GENERIC_EMULATOR) && Build.DEVICE.lowercase().startsWith(GENERIC_EMULATOR))
                || Build.BOARD.lowercase().contains("nox")
                || Build.BOOTLOADER.lowercase().contains("nox")
                || Build.FINGERPRINT.lowercase().startsWith(GENERIC_EMULATOR)
                || Build.FINGERPRINT.lowercase().startsWith("unknown")
                || Build.HARDWARE.lowercase().contains("goldfish")
                || Build.HARDWARE.lowercase().contains("nox")
                || Build.HARDWARE.lowercase().contains("ranchu")
                || Build.HARDWARE.lowercase().contains("vbox64")
                || Build.HARDWARE.lowercase().contains("vbox86")
                || Build.MANUFACTURER.lowercase().contains("Genymotion".lowercase())
                || Build.MODEL.lowercase().contains("droid4x")
                || Build.MODEL.lowercase().contains("google_sdk")
                || Build.MODEL.lowercase().contains("sdk_gphone")
                || Build.MODEL.lowercase().contains("Android SDK built for".lowercase())
                || Build.MODEL.lowercase().contains("Emulator".lowercase())
                || Build.PRODUCT.lowercase().contains("emulator")
                || Build.PRODUCT.lowercase().contains("generic_64")
                || Build.PRODUCT.lowercase().contains("generic_x86")
                || Build.PRODUCT.lowercase().contains("google_sdk")
                || Build.PRODUCT.lowercase().contains("simulator")
                || Build.PRODUCT.lowercase().contains("nox")
                || Build.PRODUCT.lowercase().contains("sdk")
                || Build.PRODUCT.lowercase().contains("vbox64")
                || Build.PRODUCT.lowercase().contains("vbox86")
    }

    override fun isRootedDevice(): Boolean {

        val rootBeer: RootBeer = weakAppContext.get()?.let(::RootBeer) ?: return false
        return rootBeer.isRooted
    }

    private companion object {

        private const val DEFAULT_PORT = 27042
        private const val CERT_OK = 1
        private const val ALL_FILES_OK = 2
        private const val LOCALHOST = "localhost"
        private const val GENERIC_EMULATOR = "generic"
    }
}
