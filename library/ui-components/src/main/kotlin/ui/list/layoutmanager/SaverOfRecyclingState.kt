package pe.com.scotiabank.blpm.android.ui.list.layoutmanager

import androidx.recyclerview.widget.RecyclerView

object SaverOfRecyclingState {

    @JvmStatic
    fun save(recycling: Recycling, recyclerView: RecyclerView) {
        recycling.recyclingState = recyclerView.layoutManager?.onSaveInstanceState()
        recyclerView.layoutManager = null
        recyclerView.adapter = null
        recyclerView.clearOnScrollListeners()
    }
}
