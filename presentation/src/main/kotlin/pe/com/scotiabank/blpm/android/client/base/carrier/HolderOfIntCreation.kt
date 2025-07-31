package pe.com.scotiabank.blpm.android.client.base.carrier

import androidx.lifecycle.viewmodel.CreationExtras

class HolderOfIntCreation(extras: CreationExtras) {

    private val valuesByIdName: Map<String, Int> = createValuesByIdName(extras)

    private fun createValuesByIdName(extras: CreationExtras): Map<String, Int> {
        val intIdNames: Array<out String> = extras[CREATION_KEY_OF_INT_ID_NAMES].orEmpty()
        val intValues: IntArray = extras[CREATION_KEY_OF_INT_VALUES] ?: intArrayOf()
        return intValues
            .zip(intIdNames, ::reverse)
            .toMap()
    }

    private fun reverse(value: Int, idName: String): Pair<String, Int> = Pair(idName, value)

    fun findBy(idName: String): Int = valuesByIdName[idName] ?: 0
}
