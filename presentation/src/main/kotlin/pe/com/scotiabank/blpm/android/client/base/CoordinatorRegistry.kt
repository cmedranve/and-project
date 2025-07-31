package pe.com.scotiabank.blpm.android.client.base

interface CoordinatorRegistry {

    val children: Collection<Coordinator>

    val currentChild: Coordinator

    val currentDeepChild: Coordinator
}
