@file:JvmName("IntentExtensions")

package pe.com.scotiabank.blpm.android.client.base.carrier

import android.content.Intent
import android.os.Parcelable

fun Intent.putAllFrom(carrier: CarrierOfActivityDestination): Intent = this
    .putBooleansFrom(carrier)
    .putIntsFrom(carrier)
    .putLongsFrom(carrier)
    .putStringsFrom(carrier)
    .putParcelablesFrom(carrier)
    .addFlags(carrier)

fun Intent.putBooleansFrom(carrier: CarrierOfActivityDestination): Intent {

    val anyBooleansByIdName: Map<String, Boolean?> = carrier.anyBooleansByIdName
    anyBooleansByIdName.forEach(this::putExtra)

    val booleanIdNames: Array<String> = anyBooleansByIdName
        .keys
        .toTypedArray()

    val booleanValues: List<Boolean> = anyBooleansByIdName
        .values
        .filterNotNull()

    val booleanValuesAsArray = BooleanArray(size = booleanValues.size)
    booleanValues.onEachIndexed(booleanValuesAsArray::set)

    return this.putExtra(INTENT_KEY_OF_BOOLEAN_ID_NAMES, booleanIdNames)
        .putExtra(INTENT_KEY_OF_BOOLEAN_VALUES, booleanValuesAsArray)
}

fun Intent.putIntsFrom(carrier: CarrierOfActivityDestination): Intent {

    val anyIntsByIdName: Map<String, Int?> = carrier.anyIntsByIdName
    anyIntsByIdName.forEach(this::putExtra)

    val intIdNames: Array<String> = anyIntsByIdName
        .keys
        .toTypedArray()

    val intValues: List<Int> = anyIntsByIdName
        .values
        .filterNotNull()

    val intValuesAsArray = IntArray(size = intValues.size)
    intValues.onEachIndexed(intValuesAsArray::set)

    return this.putExtra(INTENT_KEY_OF_INT_ID_NAMES, intIdNames)
        .putExtra(INTENT_KEY_OF_INT_VALUES, intValuesAsArray)
}

fun Intent.putLongsFrom(carrier: CarrierOfActivityDestination): Intent {

    val anyLongsByIdName: Map<String, Long?> = carrier.anyLongsByIdName
    anyLongsByIdName.forEach(this::putExtra)

    val longIdNames: Array<String> = anyLongsByIdName
        .keys
        .toTypedArray()

    val longValues: List<Long> = anyLongsByIdName
        .values
        .filterNotNull()

    val longValuesAsArray = LongArray(size = longValues.size)
    longValues.onEachIndexed(longValuesAsArray::set)

    return this.putExtra(INTENT_KEY_OF_LONG_ID_NAMES, longIdNames)
        .putExtra(INTENT_KEY_OF_LONG_VALUES, longValuesAsArray)
}

fun Intent.putStringsFrom(carrier: CarrierOfActivityDestination): Intent {

    val anyStringsByIdName: Map<String, String?> = carrier.anyStringsByIdName
    anyStringsByIdName.forEach(this::putExtra)

    val stringIdNames: Array<String> = anyStringsByIdName
        .keys
        .toTypedArray()

    val stringValuesAsArray: Array<String?> = anyStringsByIdName
        .values
        .filterNotNull()
        .toTypedArray()

    return this.putExtra(INTENT_KEY_OF_STRING_ID_NAMES, stringIdNames)
        .putExtra(INTENT_KEY_OF_STRING_VALUES, stringValuesAsArray)
}

private fun Intent.putParcelablesFrom(carrier: CarrierOfActivityDestination): Intent {

    val anyParcelablesByIdName: Map<String, Parcelable?> = carrier.anyParcelablesByIdName
    anyParcelablesByIdName.forEach(this::putExtra)

    val parcelableIdNames: Array<String> = anyParcelablesByIdName
        .keys
        .toTypedArray()

    val parcelableValuesAsArray: Array<Parcelable?> = anyParcelablesByIdName
        .values
        .filterNotNull()
        .toTypedArray()

    return this.putExtra(INTENT_KEY_OF_PARCELABLE_ID_NAMES, parcelableIdNames)
        .putExtra(INTENT_KEY_OF_PARCELABLE_VALUES, parcelableValuesAsArray)
}

private fun Intent.addFlags(carrier: CarrierOfActivityDestination): Intent {

    val flags: Set<Int> = carrier.flags
    flags.forEach(this::addFlags)

    return this
}
