package pe.com.scotiabank.blpm.android.client.base.carrier

import androidx.lifecycle.viewmodel.CreationExtras

class HolderOfStringCreation(extras: CreationExtras) {

    private val valuesByIdName: Map<String, String> = createValuesByIdName(extras)

    private fun createValuesByIdName(extras: CreationExtras): Map<String, String> {
        val parcelableIdNames: Array<out String> = extras[CREATION_KEY_OF_STRING_ID_NAMES].orEmpty()
        val parcelableValues: Array<out String> = extras[CREATION_KEY_OF_STRING_VALUES].orEmpty()
        return parcelableIdNames
            .zip(parcelableValues)
            .toMap()
    }

    fun findBy(idName: String): String = valuesByIdName[idName].orEmpty()
}
