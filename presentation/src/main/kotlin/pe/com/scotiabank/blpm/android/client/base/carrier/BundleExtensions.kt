package pe.com.scotiabank.blpm.android.client.base.carrier

import android.os.Bundle
import android.os.Parcelable

fun Bundle.putAllFrom(carrier: CarrierOfFragmentDestination): Bundle = this
    .putBooleansFrom(carrier)
    .putIntsFrom(carrier)
    .putLongsFrom(carrier)
    .putStringsFrom(carrier)
    .putParcelablesFrom(carrier)

private fun Bundle.putBooleansFrom(carrier: CarrierOfFragmentDestination): Bundle {

    val anyBooleansByIdName: Map<String, Boolean?> = carrier.anyBooleansByIdName
    for (booleanByIdName in anyBooleansByIdName) {
        val value: Boolean = booleanByIdName.value ?: continue
        this.putBoolean(booleanByIdName.key, value)
    }

    val booleanIdNames: Array<String> = anyBooleansByIdName
        .keys
        .toTypedArray()

    val booleanValues: List<Boolean> = anyBooleansByIdName
        .values
        .filterNotNull()

    val booleanValuesAsArray = BooleanArray(size = booleanValues.size)
    booleanValues.onEachIndexed(booleanValuesAsArray::set)

    this.putStringArray(INTENT_KEY_OF_BOOLEAN_ID_NAMES, booleanIdNames)
    this.putBooleanArray(INTENT_KEY_OF_BOOLEAN_VALUES, booleanValuesAsArray)

    return this
}

private fun Bundle.putIntsFrom(carrier: CarrierOfFragmentDestination): Bundle {

    val anyIntsByIdName: Map<String, Int?> = carrier.anyIntsByIdName
    for (intByIdName in anyIntsByIdName) {
        val value: Int = intByIdName.value ?: continue
        this.putInt(intByIdName.key, value)
    }

    val intIdNames: Array<String> = anyIntsByIdName
        .keys
        .toTypedArray()

    val intValues: List<Int> = anyIntsByIdName
        .values
        .filterNotNull()

    val intValuesAsArray = IntArray(size = intValues.size)
    intValues.onEachIndexed(intValuesAsArray::set)

    this.putStringArray(INTENT_KEY_OF_INT_ID_NAMES, intIdNames)
    this.putIntArray(INTENT_KEY_OF_INT_VALUES, intValuesAsArray)

    return this
}

private fun Bundle.putLongsFrom(carrier: CarrierOfFragmentDestination): Bundle {

    val anyLongsByIdName: Map<String, Long?> = carrier.anyLongsByIdName
    for (longByIdName in anyLongsByIdName) {
        val value: Long = longByIdName.value ?: continue
        this.putLong(longByIdName.key, value)
    }

    val longIdNames: Array<String> = anyLongsByIdName
        .keys
        .toTypedArray()

    val longValues: List<Long> = anyLongsByIdName
        .values
        .filterNotNull()

    val longValuesAsArray = LongArray(size = longValues.size)
    longValues.onEachIndexed(longValuesAsArray::set)

    this.putStringArray(INTENT_KEY_OF_LONG_ID_NAMES, longIdNames)
    this.putLongArray(INTENT_KEY_OF_LONG_VALUES, longValuesAsArray)

    return this
}

private fun Bundle.putStringsFrom(carrier: CarrierOfFragmentDestination): Bundle {

    val anyStringsByIdName: Map<String, String?> = carrier.anyStringsByIdName
    for (stringByIdName in anyStringsByIdName) {
        val value: String = stringByIdName.value ?: continue
        this.putString(stringByIdName.key, value)
    }

    val stringIdNames: Array<String> = anyStringsByIdName
        .keys
        .toTypedArray()

    val stringValuesAsArray: Array<String?> = anyStringsByIdName
        .values
        .filterNotNull()
        .toTypedArray()

    this.putStringArray(INTENT_KEY_OF_STRING_ID_NAMES, stringIdNames)
    this.putStringArray(INTENT_KEY_OF_STRING_VALUES, stringValuesAsArray)

    return this
}

private fun Bundle.putParcelablesFrom(carrier: CarrierOfFragmentDestination): Bundle {

    val anyParcelablesByIdName: Map<String, Parcelable?> = carrier.anyParcelablesByIdName
    for (parcelableByIdName in anyParcelablesByIdName) {
        val value: Parcelable = parcelableByIdName.value ?: continue
        this.putParcelable(parcelableByIdName.key, value)
    }

    val parcelableIdNames: Array<String> = anyParcelablesByIdName
        .keys
        .toTypedArray()

    val parcelableValuesAsArray: Array<Parcelable?> = anyParcelablesByIdName
        .values
        .filterNotNull()
        .toTypedArray()

    this.putStringArray(INTENT_KEY_OF_PARCELABLE_ID_NAMES, parcelableIdNames)
    this.putParcelableArray(INTENT_KEY_OF_PARCELABLE_VALUES, parcelableValuesAsArray)

    return this
}
