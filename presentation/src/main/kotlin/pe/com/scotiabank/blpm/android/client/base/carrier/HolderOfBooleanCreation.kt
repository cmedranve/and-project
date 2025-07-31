package pe.com.scotiabank.blpm.android.client.base.carrier

import androidx.lifecycle.viewmodel.CreationExtras

class HolderOfBooleanCreation(extras: CreationExtras) {

    private val valuesByIdName: Map<String, Boolean> = createValuesByIdName(extras)

    private fun createValuesByIdName(extras: CreationExtras): Map<String, Boolean> {
        val booleanIdNames: Array<out String> = extras[CREATION_KEY_OF_BOOLEAN_ID_NAMES].orEmpty()
        val booleanValues: BooleanArray = extras[CREATION_KEY_OF_BOOLEAN_VALUES] ?: booleanArrayOf()
        return booleanValues
            .zip(booleanIdNames, ::reverse)
            .toMap()
    }

    private fun reverse(value: Boolean, idName: String): Pair<String, Boolean> = Pair(idName, value)

    fun findBy(idName: String): Boolean = valuesByIdName[idName] ?: false
}
