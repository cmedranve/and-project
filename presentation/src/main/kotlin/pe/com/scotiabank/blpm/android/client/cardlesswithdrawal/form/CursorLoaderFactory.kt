package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.loader.content.CursorLoader
import pe.com.scotiabank.blpm.android.client.util.Constant

object CursorLoaderFactory {

    @Suppress("UNUSED_PARAMETER")
    @JvmStatic
    fun createForId(appContext: Context, contactUri: Uri, contactId: String, fieldName: String): CursorLoader {
        val projection: Array<String> = arrayOf(fieldName)
        val selection: String = Constant.EMPTY_STRING
        val selectionArgs: Array<String> = arrayOf()
        return CursorLoader(appContext, contactUri, projection, selection, selectionArgs, fieldName)
    }

    @Suppress("UNUSED_PARAMETER")
    @JvmStatic
    fun createForStructuredName(appContext: Context, contactUri: Uri, contactId: String, fieldName: String): CursorLoader {
        val dataUri: Uri = ContactsContract.Data.CONTENT_URI
        val projection: Array<String> = arrayOf(fieldName)
        val selection = buildSelection(ContactsContract.Data.MIMETYPE)
        val selectionArgs = buildSelectionArgs(contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        return CursorLoader(appContext, dataUri, projection, selection, selectionArgs, fieldName)
    }

    @Suppress("UNUSED_PARAMETER")
    @JvmStatic
    fun createForContactNumber(appContext: Context, contactUri: Uri, contactId: String, fieldName: String): CursorLoader {
        val phoneUri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection: Array<String> = arrayOf(fieldName)
        val selection = buildSelection(ContactsContract.CommonDataKinds.Phone.TYPE)
        val selectionArgs = buildSelectionArgs(contactId, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE.toString())
        return CursorLoader(appContext, phoneUri, projection, selection, selectionArgs, fieldName)
    }

    @JvmStatic
    private fun buildSelection(
        fieldTypeOfFilter: String
    ): String = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ? AND $fieldTypeOfFilter = ?"

    @JvmStatic
    private fun buildSelectionArgs(contactId: String, fieldNameOfFilter: String): Array<String> = arrayOf(
        contactId,
        fieldNameOfFilter
    )

    @Suppress("UNUSED_PARAMETER")
    @JvmStatic
    fun createForEmpty(appContext: Context, lookupUri: Uri, contactId: String, fieldName: String): CursorLoader {
        val projection: Array<String> = arrayOf(Constant.EMPTY_STRING)
        val selection = Constant.EMPTY_STRING
        val selectionArgs: Array<String> = arrayOf(Constant.EMPTY_STRING)
        return CursorLoader(appContext, lookupUri, projection, selection, selectionArgs, fieldName)
    }
}
