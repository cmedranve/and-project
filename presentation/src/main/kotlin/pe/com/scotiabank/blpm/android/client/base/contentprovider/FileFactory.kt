package pe.com.scotiabank.blpm.android.client.base.contentprovider

import android.content.Context
import android.os.Environment
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.io.File
import java.lang.ref.WeakReference

object FileFactory {

    @JvmStatic
    fun attemptCreate(
        weakAppContext: WeakReference<out Context?>,
        registry: SecuredDirectoryRegistry,
        filePrefix: String,
        mainName: String,
        fileType: String
    ): File? {
        val isPrefixAllowed: Boolean = filePrefix.matches(Constant.ALPHANUMERIC_SPACE_HYPHEN_PATTERN.toRegex())
        if (!isPrefixAllowed) return null

        val isMainAllowed: Boolean = mainName.matches(Constant.ALPHANUMERIC_SPACE_HYPHEN_PATTERN.toRegex())
        if (!isMainAllowed) return null

        val isTypeAllowed: Boolean = fileType.equals(Constant.PDF_TYPE, true)
            || fileType.equals(Constant.PNG_TYPE, true)
        if (!isTypeAllowed) return null

        val filename: String = buildFilename(filePrefix, mainName, fileType)
        val dir: File = attemptFindDir(weakAppContext, registry) ?: return null
        return File(dir, filename)
    }

    @JvmStatic
    private fun buildFilename(filePrefix: String, mainName: String, fileType: String): String {
        var filename = ".$fileType"
        if (mainName.isNotBlank()) {
            filename = mainName + filename
        }
        if (filePrefix.isNotBlank()) {
            filename = "$filePrefix${Constant.SPACE_WHITE}" + filename
        }
        return filename
    }

    @JvmStatic
    fun attemptFindDir(
        weakAppContext: WeakReference<out Context?>,
        registry: SecuredDirectoryRegistry,
    ): File? {
        val isSecuredDirAllowed: Boolean = registry
            .securedDirPath
            .matches(
                Constant.LETTER_DASH_PATTERN.toRegex()
            )
        if (!isSecuredDirAllowed) return null

        val externalFilesDir: File = weakAppContext.get()?.let(::findExternalFilesDir) ?: return null
        return File(externalFilesDir, registry.securedDirPath)
    }

    @JvmStatic
    private fun findExternalFilesDir(
        appContext: Context
    ): File? {
        val externalFilesDir: File = appContext.getExternalFilesDir(null) ?: return null
        val storageState: String = Environment.getExternalStorageState(externalFilesDir)
        val isMounted: Boolean = Environment.MEDIA_MOUNTED.equals(storageState, true)
        return if (isMounted) externalFilesDir else null
    }
}
