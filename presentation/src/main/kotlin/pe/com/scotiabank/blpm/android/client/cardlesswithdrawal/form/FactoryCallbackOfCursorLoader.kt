package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form

import android.content.Context
import android.net.Uri
import androidx.loader.content.CursorLoader

fun interface FactoryCallbackOfCursorLoader {

    fun create(
        appContext: Context,
        contactUri: Uri,
        contactId: String,
        fieldName: String
    ): CursorLoader
}
