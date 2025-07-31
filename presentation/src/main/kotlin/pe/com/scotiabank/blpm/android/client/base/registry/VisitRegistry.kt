package pe.com.scotiabank.blpm.android.client.base.registry

import java.util.concurrent.ConcurrentHashMap

class VisitRegistry(private val maxNumberAllowedById: Map<Long, Int>) {

    private val numberOfVisitById: MutableMap<Long, Int?> = maxNumberAllowedById.mapValuesTo(
        destination = ConcurrentHashMap(),
        transform = ::toZero,
    )

    @Suppress("UNUSED_PARAMETER")
    private fun toZero(entry: Map.Entry<Long, Int>): Int = 0

    fun isVisitAllowed(id: Long): Boolean {
        val maxNumberAllowed: Int = maxNumberAllowedById[id] ?: return false
        var numberOfVisit: Int = numberOfVisitById[id] ?: return false
        numberOfVisitById[id] = ++numberOfVisit
        return numberOfVisit <= maxNumberAllowed
    }

    fun findNumberOfVisits(id: Long): Int = numberOfVisitById[id] ?: 0

    fun resetAttemptsById(id: Long) {
        numberOfVisitById[id] = 0
    }
}
