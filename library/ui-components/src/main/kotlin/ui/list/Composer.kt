package pe.com.scotiabank.blpm.android.ui.list

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import pe.com.scotiabank.blpm.android.ui.list.animation.DisablerOfChangeAnimation
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.RecyclerRendering
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.EventHandler

object Composer {

    @JvmStatic
    fun compose(
        owner: LifecycleOwner,
        recyclerView: RecyclerView,
        liveCompounds: LiveData<List<UiCompound<*>>>,
    ) {
        DisablerOfChangeAnimation.disable(recyclerView)
        val viewPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(viewPool)
        observeLiveCompounds(
            owner = owner,
            viewPool = viewPool,
            recyclerView = recyclerView,
            liveCompounds = liveCompounds,
            scrollingEventHandler = null,
        )
    }

    @JvmStatic
    fun observeLiveCompounds(
        owner: LifecycleOwner,
        viewPool: RecyclerView.RecycledViewPool,
        recyclerView: RecyclerView,
        liveCompounds: LiveData<List<UiCompound<*>>>,
        scrollingEventHandler: EventHandler?,
    ) {
        liveCompounds.observe(owner) { compounds ->

            val compoundsById: Map<Long, UiCompound<*>> = compounds.associateByTo(
                destination = LinkedHashMap(),
                keySelector = ::byId,
            )
            RecyclerRendering.renderFrom(
                compoundsById = compoundsById,
                scrollingEventHandler = scrollingEventHandler,
                viewPool = viewPool,
                recyclerView = recyclerView,
            )
        }
    }
}
