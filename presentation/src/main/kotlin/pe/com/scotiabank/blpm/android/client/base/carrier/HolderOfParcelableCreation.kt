package pe.com.scotiabank.blpm.android.client.base.carrier

import android.os.Parcelable
import androidx.lifecycle.viewmodel.CreationExtras

class HolderOfParcelableCreation(extras: CreationExtras) {

    private val valuesByIdName: Map<String, Parcelable> = createValuesByIdName(extras)

    private fun createValuesByIdName(extras: CreationExtras): Map<String, Parcelable> {
        val parcelableIdNames: Array<out String> = extras[CREATION_KEY_OF_PARCELABLE_ID_NAMES].orEmpty()
        val parcelableValues: Array<out Parcelable> = extras[CREATION_KEY_OF_PARCELABLE_VALUES].orEmpty()
        return parcelableIdNames
            .zip(parcelableValues)
            .toMap()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Parcelable> findBy(idName: String): T = valuesByIdName[idName] as? T
        ?: throw IllegalArgumentException("Expected extra argument not found in " + this::class.simpleName)

    @Suppress("UNCHECKED_CAST")
    fun <T : Parcelable> getBy(idName: String): T? = valuesByIdName[idName] as? T
}
