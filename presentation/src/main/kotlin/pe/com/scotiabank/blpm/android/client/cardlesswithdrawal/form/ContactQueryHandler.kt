package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form

import android.database.Cursor
import android.provider.ContactsContract
import pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form.PhoneNumberUtil.removeWhitespaces
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.lang.ref.WeakReference

object ContactQueryHandler {

    @JvmStatic
    fun queryContactId(cursor: Cursor?, weakConsumer: WeakReference<ContactQueryTemplate?>) {
        val id = retrieveBy(cursor, ContactsContract.Contacts._ID)
        weakConsumer.get()?.onContactId(id)
    }

    @JvmStatic
    private fun retrieveBy(cursor: Cursor?, fieldName: String): String {
        if (cursor == null) return Constant.EMPTY_STRING

        if (cursor.moveToNext()) {
            val columnIndex: Int = cursor.getColumnIndex(fieldName)
            if (Constant.NON_EXISTING_COLUMN_INDEX == columnIndex) return Constant.EMPTY_STRING

            return cursor.getString(columnIndex).orEmpty()
        }

        return Constant.EMPTY_STRING
    }

    @JvmStatic
    fun queryFirstName(cursor: Cursor?, weakConsumer: WeakReference<ContactQueryTemplate?>) {
        var name = retrieveBy(cursor, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)
        name = stringMax30(name)
        weakConsumer.get()?.onFirstName(name)
    }

    @JvmStatic
    fun queryLastName(cursor: Cursor?, weakConsumer: WeakReference<ContactQueryTemplate?>) {
        var name = retrieveBy(cursor, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
        name = stringMax30(name)
        weakConsumer.get()?.onLastName(name)
    }

    @JvmStatic
    private fun stringMax30(value: String): String {
        if (value.length > 30) return value.substring(0, 30)
        return value
    }

    @JvmStatic
    fun queryContactNumber(cursor: Cursor?, weakConsumer: WeakReference<ContactQueryTemplate?>) {
        if (cursor == null) return

        while (cursor.moveToNext()) {
            val columnIndexOfNumber: Int = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            if (Constant.NON_EXISTING_COLUMN_INDEX == columnIndexOfNumber) continue

            val contactNumber: String = cursor
                .getString(columnIndexOfNumber)
                .orEmpty()

            val cleanNumber: String = contactNumber
                .trim()
                .let(PhoneNumberUtil::removeCountryCode)
                .removeWhitespaces()

            if (!PhoneNumberUtil.isValid(cleanNumber)) continue

            weakConsumer.get()?.onPhoneNumber(cleanNumber)
            return
        }

        weakConsumer.get()?.onPhoneNumber(Constant.EMPTY_STRING)
    }
}
