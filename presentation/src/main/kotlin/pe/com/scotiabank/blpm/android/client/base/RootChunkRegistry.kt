package pe.com.scotiabank.blpm.android.client.base

import java.util.Deque
import java.util.concurrent.ConcurrentLinkedDeque

class RootChunkRegistry(private val chunks: Deque<Chunk> = ConcurrentLinkedDeque()) {

    fun addChunk(chunk: Chunk) {
        chunks.push(chunk)
    }

    suspend fun removeChunks() {
        chunks.forEach { chunk -> removeChunk(chunk) }
    }

    suspend fun removeChunk(chunk: Chunk) {
        val retrievedChunk: Chunk = chunks.peekFirst() ?: return
        val isFound: Boolean = retrievedChunk.id == chunk.id
        if (isFound.not()) return

        chunk.clearChunk()
        chunks.pop()
    }
}
