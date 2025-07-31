package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form

import android.database.Cursor
import java.lang.ref.WeakReference

fun interface QueryCallback {

    fun query(cursor: Cursor?, weakConsumer: WeakReference<ContactQueryTemplate?>)
}
