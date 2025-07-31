package pe.com.scotiabank.blpm.android.client.base

interface ChunkRegistry {

    val children: Collection<Chunk>

    val currentChild: Chunk

    val currentDeepChild: Chunk
}
