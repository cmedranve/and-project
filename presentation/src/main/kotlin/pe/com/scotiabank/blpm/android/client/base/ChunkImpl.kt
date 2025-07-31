package pe.com.scotiabank.blpm.android.client.base

import android.os.Parcelable
import androidx.core.util.Supplier
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.util.KeyboardIntention
import java.lang.ref.WeakReference

abstract class ChunkImpl(
    private val weakCoordinator: WeakReference<out Coordinator?>,
    protected val weakParent: WeakReference<out Chunk?>,
    protected val scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    private val userInterface: InstanceReceiver,
) : Chunk, DispatcherProvider by dispatcherProvider {

    private val closeableCoroutineScope: CloseableCoroutineScope = CloseableCoroutineScope(scope)

    open val selfSuspendingReceiver: SuspendingReceiverOfInstance? = null

    private val chunkItemRegistry: ChunkItemRegistry = ChunkItemRegistry()
    protected var selectedChunkItemId: Long by chunkItemRegistry::selectedItemId

    private val chunkRegistry: ChunkRegistryImpl by lazy {
        ChunkRegistryImpl(
            parent = this,
            chunkItemRegistry = chunkItemRegistry,
        )
    }

    override val children: Collection<Chunk>
        get() = chunkRegistry.children

    protected val childrenCount: Int
        get() = chunkRegistry.childrenCount

    override val currentChild: Chunk
        get() = chunkRegistry.currentChild

    override val currentDeepChild: Chunk
        get() = chunkRegistry.currentDeepChild

    override var recyclingState: Parcelable?
        get() = chunkRegistry.recyclingState
        set(value) {
            chunkRegistry.recyclingState = value
        }

    protected fun hideKeyboard() {
        userInterface.receive(KeyboardIntention.HIDE)
    }

    protected fun showKeyboard() {
        userInterface.receive(KeyboardIntention.SHOW)
    }

    override suspend fun receiveFromChild(event: Any) {
        val isHandled: Boolean = selfSuspendingReceiver?.receive(event) ?: false
        if (isHandled) return

        val isRoot: Boolean = weakParent.get() == null
        if (isRoot.not()) {
            weakParent.get()?.receiveFromChild(event)
            return
        }

        weakCoordinator.get()?.receiveFromChild(event)
    }

    override suspend fun receiveFromAncestor(event: Any) {
        selfSuspendingReceiver?.receive(event)
    }

    protected fun addChild(child: Chunk) {
        chunkRegistry.addChild(child)
    }

    protected fun addChunkItemFactory(id: Long, factory: Supplier<Chunk>) {
        chunkItemRegistry.addItemFactory(id, factory)
    }

    protected fun setDefaultChunkItem(id: Long) {
        chunkItemRegistry.defaultItemId = id
    }

    override suspend fun clearChunk() {
        val allChildrenAndSelf: List<Chunk> = findAllChildrenAndSelf()
        val allDeferredClearing: List<Deferred<Unit>> = allChildrenAndSelf
            .map { child: Chunk -> deferClearingChild(child) }
        allDeferredClearing.awaitAll()
        chunkItemRegistry.removeAllItems()
        tryClosingScope()
    }

    private suspend fun findAllChildrenAndSelf(): List<Chunk> = withContext(defaultDispatcher) {
        val allChildrenAndSelf: MutableList<Chunk> = mutableListOf()
        children
            .reversed()
            .flatMapTo(destination = allChildrenAndSelf, transform = ::toChildren)

        allChildrenAndSelf.addAll(chunkItemRegistry.nonSelectedItems)
        chunkItemRegistry.selectedItem?.let(allChildrenAndSelf::add)

        allChildrenAndSelf.add(element = this@ChunkImpl)
        allChildrenAndSelf
    }

    private fun toChildren(child: Chunk): List<Chunk> = child
        .children
        .reversed()
        .flatMap(transform = ::toChildren)

    private suspend fun deferClearingChild(
        child: Chunk,
    ): Deferred<Unit> = withContext(mainDispatcher) {

        async { child.onChunkCleared() }
    }

    private fun tryClosingScope() {
        try {
            closeableCoroutineScope.close()
        } catch (throwable: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }
}
