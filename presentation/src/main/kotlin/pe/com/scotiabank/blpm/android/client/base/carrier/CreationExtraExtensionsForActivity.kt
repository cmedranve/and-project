@file:JvmName("CreationExtraExtensionsForActivity")

package pe.com.scotiabank.blpm.android.client.base.carrier

import android.content.Intent
import android.os.Parcelable
import androidx.lifecycle.viewmodel.MutableCreationExtras
import pe.com.scotiabank.blpm.android.client.util.parcelableArray

fun MutableCreationExtras.feedFrom(intent: Intent?) {
    val nonNullIntent: Intent = intent ?: return
    feedWithBooleansFrom(nonNullIntent)
    feedWithIntsFrom(nonNullIntent)
    feedWithLongsFrom(nonNullIntent)
    feedWithStringsFrom(nonNullIntent)
    feedWithParcelablesFrom(nonNullIntent)
}

private fun MutableCreationExtras.feedWithBooleansFrom(intent: Intent) {

    val idNames: Array<out String> = intent
        .getStringArrayExtra(INTENT_KEY_OF_BOOLEAN_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_BOOLEAN_ID_NAMES] = idNames

    val values: BooleanArray = intent
        .getBooleanArrayExtra(INTENT_KEY_OF_BOOLEAN_VALUES)
        ?: booleanArrayOf()
    this[CREATION_KEY_OF_BOOLEAN_VALUES] = values
}

private fun MutableCreationExtras.feedWithIntsFrom(intent: Intent) {

    val idNames: Array<out String> = intent
        .getStringArrayExtra(INTENT_KEY_OF_INT_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_INT_ID_NAMES] = idNames

    val values: IntArray = intent
        .getIntArrayExtra(INTENT_KEY_OF_INT_VALUES)
        ?: intArrayOf()
    this[CREATION_KEY_OF_INT_VALUES] = values
}

private fun MutableCreationExtras.feedWithLongsFrom(intent: Intent) {

    val idNames: Array<out String> = intent
        .getStringArrayExtra(INTENT_KEY_OF_LONG_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_LONG_ID_NAMES] = idNames

    val values: LongArray = intent
        .getLongArrayExtra(INTENT_KEY_OF_LONG_VALUES)
        ?: longArrayOf()
    this[CREATION_KEY_OF_LONG_VALUES] = values
}

private fun MutableCreationExtras.feedWithStringsFrom(intent: Intent) {

    val idNames: Array<out String> = intent
        .getStringArrayExtra(INTENT_KEY_OF_STRING_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_STRING_ID_NAMES] = idNames

    val values: Array<out String> = intent
        .getStringArrayExtra(INTENT_KEY_OF_STRING_VALUES)
        .orEmpty()
    this[CREATION_KEY_OF_STRING_VALUES] = values
}

private fun MutableCreationExtras.feedWithParcelablesFrom(intent: Intent) {

    val idNames: Array<out String> = intent
        .getStringArrayExtra(INTENT_KEY_OF_PARCELABLE_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_PARCELABLE_ID_NAMES] = idNames

    val values: Array<out Parcelable> = intent
        .parcelableArray<Parcelable>(INTENT_KEY_OF_PARCELABLE_VALUES)
        .orEmpty()
    this[CREATION_KEY_OF_PARCELABLE_VALUES] = values
}
