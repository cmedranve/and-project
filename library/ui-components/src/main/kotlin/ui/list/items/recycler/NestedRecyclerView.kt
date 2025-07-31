package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.R
import androidx.recyclerview.widget.RecyclerView

class NestedRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.recyclerViewStyle,
) : RecyclerView(context, attrs, defStyleAttr) {

    private val copyOfOnItemTouchListeners: MutableList<OnItemTouchListener> = mutableListOf()

    override fun addOnItemTouchListener(listener: OnItemTouchListener) {
        copyOfOnItemTouchListeners.add(listener)
        super.addOnItemTouchListener(listener)
    }

    override fun removeOnItemTouchListener(listener: OnItemTouchListener) {
        copyOfOnItemTouchListeners.remove(listener)
        super.removeOnItemTouchListener(listener)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {

        val isIntercepted: Boolean = e?.let(::isInterceptedByOnItemTouchListener) ?: false
        if (isIntercepted) return super.onTouchEvent(e)

        if (isNestedScrollingEnabled) return super.onTouchEvent(e)

        return false
    }

    /**
     * Checks whether the MotionEvent is intercepted by an OnItemTouchListener or not.
     *
     *
     * Calls [RecyclerView.OnItemTouchListener.onInterceptTouchEvent] on each of the registered
     * [RecyclerView.OnItemTouchListener]s, passing in the MotionEvent.
     *
     * If one returns true and the action is not ACTION_CANCEL, the whole function returns true.
     * If none has intercepted the MotionEvent or the action is ACTION_CANCEL, returns false.
     *
     * @param event The MotionEvent
     * @return true if an OnItemTouchListener has intercepted the MotionEvent.
     */
    private fun isInterceptedByOnItemTouchListener(event: MotionEvent): Boolean {

        val action: Int = event.action

        for (listener: OnItemTouchListener in copyOfOnItemTouchListeners) {

            val isIntercepted: Boolean = listener.onInterceptTouchEvent(this, event)
            if (isIntercepted.not()) continue

            val isCancelAction: Boolean = MotionEvent.ACTION_CANCEL == action
            if (isCancelAction) continue

            return true
        }

        return false
    }
}
