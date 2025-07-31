package pe.com.scotiabank.blpm.android.ui.list.adapterfactories

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.viewbinding.ViewBinding
import com.scotiabank.enhancements.weakreference.getEmptyWeak
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.EventHandler
import java.lang.ref.WeakReference

class ScrollingAgent(
    private val compoundId: Long,
    val handler: EventHandler?,
    private var lastItemId: Long = 0,
) : OnScrollListener(), Runnable {

    var weakBinding: WeakReference<out ViewBinding> = getEmptyWeak()
    private val lastItemView: View?
        get() = weakBinding.get()?.root

    private var isHandled: Boolean = false

    private val eventCarrier: ScrollingEventCarrier by lazy {
        ScrollingEventCarrier(
            event = ScrollingEvent.SCROLLED_TO_MAXIMUM_VALUE,
            compoundId = compoundId,
        )
    }

    fun onLastItemReached(itemId: Long) {
        if (lastItemId == itemId) return

        lastItemId = itemId
        isHandled = false
        lastItemView?.let(::registerCallback)
    }

    private fun registerCallback(itemView: View) {
        itemView.removeCallbacks(this)
        itemView.post(this)
    }

    override fun run() {
        val itemView: View = lastItemView ?: return
        val recyclerView: RecyclerView = itemView.parent as? RecyclerView ?: return
        val layoutManager: LayoutManager = recyclerView.layoutManager ?: return

        val isFullyVisible: Boolean = isFullyVisible(itemView, layoutManager)
        if (isFullyVisible.not()) return

        if (isHandled) return

        val isHandled: Boolean = handler?.receiveEvent(eventCarrier) ?: false
        this.isHandled = isHandled
    }

    private fun isFullyVisible(
        itemView: View,
        layoutManager: LayoutManager,
    ) : Boolean = layoutManager.isViewPartiallyVisible(itemView, true, true)

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (RecyclerView.SCROLL_STATE_IDLE != newState) return
        if (isHandled) return

        lastItemView?.let(::registerCallback)
    }
}
