package pe.com.scotiabank.blpm.android.client.app

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.PackageManager.PackageInfoFlags
import android.os.Build
import com.scotiabank.enhancements.weakreference.getEmptyWeak
import pe.com.scotiabank.blpm.android.client.assistance.ContextualAssistanceHelper
import pe.com.scotiabank.blpm.android.client.assistance.StatusBadgeHelper
import pe.com.scotiabank.blpm.android.client.assistance.model.ContextualAssistance
import pe.com.scotiabank.blpm.android.client.assistance.model.StatusBadge
import java.lang.ref.WeakReference

class MutableStoreOfAppPackageInfo(weakAppContext: WeakReference<out Context?>): StoreOfAppPackageInfo {

    private val packageName: String = weakAppContext.get()?.packageName.orEmpty()
    private val weakPackageManager: WeakReference<PackageManager?> = weakAppContext
        .get()
        ?.packageManager
        ?.let(::WeakReference)
        ?: getEmptyWeak()

    override fun isFirstInstall(): Boolean {
        val packageInfo: PackageInfo = tryGettingPackageInfo() ?: return true
        val firstInstallTime: Long = packageInfo.firstInstallTime
        val lastUpdateTime: Long = packageInfo.lastUpdateTime
        return firstInstallTime == lastUpdateTime
    }

    private fun tryGettingPackageInfo(): PackageInfo? = try {
        weakPackageManager.get()?.let(::getPackageInfo)
    } catch (exception: NameNotFoundException) {
        null
    }

    private fun getPackageInfo(packageManager: PackageManager): PackageInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val flags: PackageInfoFlags = PackageInfoFlags.of(FLAG_TO_OMIT_ADDITIONAL_DATA.toLong())
            return packageManager.getPackageInfo(packageName, flags)
        }
        return packageManager.getPackageInfo(packageName, FLAG_TO_OMIT_ADDITIONAL_DATA)
    }

    companion object {

        private const val FLAG_TO_OMIT_ADDITIONAL_DATA = 0
    }
}

class MutableStoreOfAssistanceUi: StoreOfAssistanceUi {

    override val contextualAssistance: ContextualAssistance by lazy {
        ContextualAssistanceHelper.loadContextualAssistance()
    }

    override val statusBadge: StatusBadge by lazy {
        StatusBadgeHelper.loadStatusBadge()
    }
}
