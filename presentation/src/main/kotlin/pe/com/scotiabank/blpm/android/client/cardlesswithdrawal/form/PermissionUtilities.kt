package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

const val READ_CONTACTS_PERMISSION: String = Manifest.permission.READ_CONTACTS

fun isReadContactsPermissionGranted(appContext: Context): Boolean {
    return isPermissionGranted(appContext, READ_CONTACTS_PERMISSION)
}

fun isPermissionGranted(appContext: Context, permission: String): Boolean {
    val permissionResult: Int = ContextCompat.checkSelfPermission(appContext, permission)
    return PackageManager.PERMISSION_GRANTED == permissionResult
}
