@file:JvmName("CreationExtraExtensionsForFragment")

package pe.com.scotiabank.blpm.android.client.base.carrier

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.viewmodel.MutableCreationExtras
import pe.com.scotiabank.blpm.android.client.util.parcelableArray

fun MutableCreationExtras.feedFrom(arguments: Bundle?) {
    val nonNullArguments: Bundle = arguments ?: return
    feedWithBooleansFrom(nonNullArguments)
    feedWithIntsFrom(nonNullArguments)
    feedWithLongsFrom(nonNullArguments)
    feedWithStringsFrom(nonNullArguments)
    feedWithParcelablesFrom(nonNullArguments)
}

private fun MutableCreationExtras.feedWithBooleansFrom(arguments: Bundle) {

    val idNames: Array<out String> = arguments
        .getStringArray(INTENT_KEY_OF_BOOLEAN_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_BOOLEAN_ID_NAMES] = idNames

    val values: BooleanArray = arguments
        .getBooleanArray(INTENT_KEY_OF_BOOLEAN_VALUES)
        ?: booleanArrayOf()
    this[CREATION_KEY_OF_BOOLEAN_VALUES] = values
}

private fun MutableCreationExtras.feedWithIntsFrom(arguments: Bundle) {

    val idNames: Array<out String> = arguments
        .getStringArray(INTENT_KEY_OF_INT_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_INT_ID_NAMES] = idNames

    val values: IntArray = arguments
        .getIntArray(INTENT_KEY_OF_INT_VALUES)
        ?: intArrayOf()
    this[CREATION_KEY_OF_INT_VALUES] = values
}

private fun MutableCreationExtras.feedWithLongsFrom(arguments: Bundle) {

    val idNames: Array<out String> = arguments
        .getStringArray(INTENT_KEY_OF_LONG_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_LONG_ID_NAMES] = idNames

    val values: LongArray = arguments
        .getLongArray(INTENT_KEY_OF_LONG_VALUES)
        ?: longArrayOf()
    this[CREATION_KEY_OF_LONG_VALUES] = values
}

private fun MutableCreationExtras.feedWithStringsFrom(arguments: Bundle) {

    val idNames: Array<out String> = arguments
        .getStringArray(INTENT_KEY_OF_STRING_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_STRING_ID_NAMES] = idNames

    val values: Array<out String> = arguments
        .getStringArray(INTENT_KEY_OF_STRING_VALUES)
        .orEmpty()
    this[CREATION_KEY_OF_STRING_VALUES] = values
}

private fun MutableCreationExtras.feedWithParcelablesFrom(arguments: Bundle) {

    val idNames: Array<out String> = arguments
        .getStringArray(INTENT_KEY_OF_PARCELABLE_ID_NAMES)
        .orEmpty()
    this[CREATION_KEY_OF_PARCELABLE_ID_NAMES] = idNames

    val values: Array<out Parcelable> = arguments
        .parcelableArray<Parcelable>(INTENT_KEY_OF_PARCELABLE_VALUES)
        .orEmpty()
    this[CREATION_KEY_OF_PARCELABLE_VALUES] = values
}
