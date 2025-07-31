package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfConcatAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.PortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.ScrollingAgent
import pe.com.scotiabank.blpm.android.ui.list.compound.Submitter
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.EventHandler

object RecyclerRendering {

    @JvmStatic
    fun renderFrom(
        compoundsById: Map<Long, UiCompound<*>>,
        scrollingEventHandler: EventHandler?,
        viewPool: RecycledViewPool,
        recyclerView: RecyclerView,
    ) {
        val oldConcatAdapter: ConcatAdapter? = recyclerView.adapter as? ConcatAdapter

        if (oldConcatAdapter == null) {
            renderInitial(compoundsById, scrollingEventHandler, viewPool, recyclerView)
            return
        }

        val oldAdapters: List<PortableAdapter<*, *>> = oldConcatAdapter
            .adapters
            .mapNotNull(::toOldPortableAdapter)

        if (compoundsById.size != oldAdapters.size) {
            renderInitial(compoundsById, scrollingEventHandler, viewPool, recyclerView)
            return
        }

        val linkedCompoundAdapters: List<LinkedCompoundAdapter<*>> = compoundsById
            .values
            .zip(oldAdapters)
            .mapNotNull { compoundAdapter -> toLinkedCompoundAdapter(compoundAdapter.first, compoundAdapter.second) }

        val isAnyAdapterMissing: Boolean = linkedCompoundAdapters.any(::isAdapterMissing)

        if (isAnyAdapterMissing) {
            renderInitial(compoundsById, scrollingEventHandler, viewPool, recyclerView)
            return
        }

        linkedCompoundAdapters.forEach { linkedCompoundAdapter -> submit(linkedCompoundAdapter) }
    }

    @JvmStatic
    private fun renderInitial(
        compoundsById: Map<Long, UiCompound<*>>,
        scrollingEventHandler: EventHandler?,
        viewPool: RecycledViewPool,
        recyclerView: RecyclerView,
    ) {
        val linkedCompoundAdapters: List<LinkedCompoundAdapter<*>> = compoundsById
            .map { compoundById -> toLinkedCompoundAdapter(compoundById.value, scrollingEventHandler, viewPool) }

        val concatAdapter: ConcatAdapter = FactoryOfConcatAdapter.create()
        linkedCompoundAdapters.forEach { compoundAdapter -> concatAdapter.addAdapter(compoundAdapter.adapter) }

        recyclerView.adapter = concatAdapter

        linkedCompoundAdapters.forEach { linkedCompoundAdapter -> submit(linkedCompoundAdapter) }
        linkedCompoundAdapters.forEach { linkedCompoundAdapter -> addOnScrollListener(linkedCompoundAdapter, recyclerView) }
    }

    @JvmStatic
    private fun <E: IdentifiableUiEntity<E>> toLinkedCompoundAdapter(
        compound: UiCompound<E>,
        scrollingEventHandler: EventHandler?,
        viewPool: RecycledViewPool,
    ): LinkedCompoundAdapter<E> {

        val scrollingAgent = ScrollingAgent(compound.id, scrollingEventHandler)
        val adapterFactory: FactoryOfPortableAdapter<E, *> = compound.factoryOfPortableAdapter
        val adapter: PortableAdapter<E, *> = adapterFactory.create(compound.id, scrollingAgent, viewPool)
        return LinkedCompoundAdapter(compound, adapter)
    }

    @JvmStatic
    private fun <E: IdentifiableUiEntity<E>> submit(linkedCompoundAdapter: LinkedCompoundAdapter<E>) {
        Submitter.submit(linkedCompoundAdapter.compound, linkedCompoundAdapter.adapter)
    }

    @JvmStatic
    private fun <E: IdentifiableUiEntity<E>> addOnScrollListener(
        linkedCompoundAdapter: LinkedCompoundAdapter<E>,
        recyclerView: RecyclerView,
    ) {
        recyclerView.addOnScrollListener(linkedCompoundAdapter.adapter.scrollingAgent)
    }

    @JvmStatic
    private fun toOldPortableAdapter(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    ): PortableAdapter<*, *>? = adapter as? PortableAdapter<*, *>

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    private fun <E: IdentifiableUiEntity<E>> toLinkedCompoundAdapter(
        compound: UiCompound<E>,
        oldAdapter: PortableAdapter<*, *>,
    ): LinkedCompoundAdapter<*>? {
        val adapter: PortableAdapter<E, *> = oldAdapter as? PortableAdapter<E, *> ?: return null
        return LinkedCompoundAdapter(compound, adapter)
    }

    @JvmStatic
    private fun isAdapterMissing(
        linkedCompoundAdapter: LinkedCompoundAdapter<*>,
    ): Boolean = linkedCompoundAdapter.compound.id != linkedCompoundAdapter.adapter.id
}
