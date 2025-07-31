package pe.com.scotiabank.blpm.android.client.base.contentprovider

import android.content.Context
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.DirectoryDeleterFacade
import pe.com.scotiabank.blpm.android.client.base.coroutine.ProviderForCoroutine
import java.io.File
import java.lang.ref.WeakReference
import kotlin.enums.EnumEntries

class DirectoryDeleter(
    private val weakAppContext: WeakReference<out Context?>,
    providerForCoroutine: ProviderForCoroutine,
): DirectoryDeleterFacade, ProviderForCoroutine by providerForCoroutine {

    override fun deleteSecuredDirectories() {
        val registries: EnumEntries<SecuredDirectoryRegistry> = SecuredDirectoryRegistry.entries
        for (registry in registries) attemptDelete(registry)
    }

    private fun attemptDelete(registry: SecuredDirectoryRegistry) {
        val appContext: Context = weakAppContext.get() ?: return
        val externalFilesDir: File? = appContext.getExternalFilesDir(null)
        val directory = File(externalFilesDir, registry.securedDirPath)
        appScope.launch {
            attemptDeletion(directory)
        }
    }

    private suspend fun attemptDeletion(file: File) = withContext(ioDispatcher) {
        try {
            executeDeletion(file)
        } catch (exception: Exception) {
            // don't required: silent error
        }
    }

    private fun executeDeletion(file: File) {
        if (file.isDirectory) {
            val children = file.listFiles() ?: return
            for (child in children) executeDeletion(child)
        }
        file.delete()
    }
}
