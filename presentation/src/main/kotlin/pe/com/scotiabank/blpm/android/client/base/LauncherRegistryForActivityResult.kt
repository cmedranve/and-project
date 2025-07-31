package pe.com.scotiabank.blpm.android.client.base

import android.content.Intent
import android.net.Uri
import androidx.activity.result.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import pe.com.scotiabank.blpm.android.client.util.CreateDocumentByType

interface LauncherRegistryForActivityResult: ActivityResultCaller {

    fun registerLauncherForActivityResult(
        callback: ActivityResultCallback<ActivityResult>
    ): ActivityResultLauncher<Intent> {
        val contract: ActivityResultContract<Intent, ActivityResult> = ActivityResultContracts.StartActivityForResult()
        return registerForActivityResult(contract, callback)
    }

    fun registerLauncherForIntentSender(
        callback: ActivityResultCallback<ActivityResult>
    ): ActivityResultLauncher<IntentSenderRequest> {
        val contract: ActivityResultContract<IntentSenderRequest, ActivityResult> = ActivityResultContracts.StartIntentSenderForResult()
        return registerForActivityResult(contract, callback)
    }

    fun registerLauncherForCreatingDocument(
        mimeType: String,
        callback: ActivityResultCallback<Uri?>,
    ): ActivityResultLauncher<String> {
        val contract: ActivityResultContract<String, Uri?> = CreateDocumentByType(mimeType)
        return registerForActivityResult(contract, callback)
    }

    fun registerLauncherForGettingContent(
        callback: ActivityResultCallback<Uri?>
    ): ActivityResultLauncher<String> {
        val contract: ActivityResultContract<String, Uri?> = ActivityResultContracts.GetContent()
        return registerForActivityResult(contract, callback)
    }

    fun registerLauncherForPickingContact(
        callback: ActivityResultCallback<Uri?>
    ): ActivityResultLauncher<Void?> {
        val contract: ActivityResultContract<Void?, Uri?> = ActivityResultContracts.PickContact()
        return registerForActivityResult(contract, callback)
    }

    fun registerLauncherForPermissionRequest(
        callback: ActivityResultCallback<Boolean>
    ): ActivityResultLauncher<String> {
        val contract: ActivityResultContract<String, Boolean> = ActivityResultContracts.RequestPermission()
        return registerForActivityResult(contract, callback)
    }

    fun registerLauncherForMultiplePermissionRequest(
        callback: ActivityResultCallback<Map<String, Boolean>>
    ): ActivityResultLauncher<Array<String>> {
        val contract: ActivityResultContract<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> = ActivityResultContracts.RequestMultiplePermissions()
        return registerForActivityResult(contract, callback)
    }
}
