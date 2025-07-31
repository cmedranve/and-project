package pe.com.scotiabank.blpm.android.client.base

import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling

interface Chunk : ChunkRegistry, DispatcherProvider, Recycling {

    val id: Long

    suspend fun start() {
        // do nothing if not required
    }

    suspend fun receiveFromChild(event: Any)

    suspend fun receiveFromAncestor(event: Any) {
        // do nothing if not required
    }

    suspend fun clearChunk() {
        // do cleanup here
    }

    /**
     * This method will be called when this Chunk is no longer used and will be destroyed.
     *
     *
     * It is useful when Chunk observes some data and you need to clear this subscription to
     * prevent a leak of this Chunk.
     */
    suspend fun onChunkCleared() {
        // called after clear()
    }
}
