package pe.com.scotiabank.blpm.android.client.base.carrier

import androidx.lifecycle.viewmodel.CreationExtras

class HolderOfLongCreation(extras: CreationExtras) {

    private val valuesByIdName: Map<String, Long> = createValuesByIdName(extras)

    private fun createValuesByIdName(extras: CreationExtras): Map<String, Long> {
        val longIdNames: Array<out String> = extras[CREATION_KEY_OF_LONG_ID_NAMES].orEmpty()
        val longValues: LongArray = extras[CREATION_KEY_OF_LONG_VALUES] ?: longArrayOf()
        return longValues
            .zip(longIdNames, ::reverse)
            .toMap()
    }

    private fun reverse(value: Long, idName: String): Pair<String, Long> = Pair(idName, value)

    fun findBy(idName: String): Long = valuesByIdName[idName] ?: 0
}
