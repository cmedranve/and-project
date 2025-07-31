package pe.com.scotiabank.blpm.android.client.base.registry

import java.util.concurrent.ConcurrentHashMap

class AvailabilityRegistry(private val ids: Collection<Long>) {

    private val availabilitiesById: MutableMap<Long, Boolean> = ids.associateTo(
        destination = ConcurrentHashMap(),
        transform = ::makeAvailable,
    )

    private fun makeAvailable(id: Long): Pair<Long, Boolean> = Pair(id, true)

    fun isAvailable(id: Long): Boolean = availabilitiesById[id] ?: true

    fun setAvailability(id: Long, availability: Boolean) {
        availabilitiesById[id] = availability
    }

    fun setAvailabilityForAll(availability: Boolean) {
        ids.forEach { id -> setAvailability(id, availability) }
    }
}
