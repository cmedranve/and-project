package pe.com.scotiabank.blpm.android.ui.list.layoutmanager

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State

class RootLayoutManager : LinearLayoutManager {

    private var afterChildrenLayout: Runnable? = null

    constructor(afterChildrenLayout: Runnable, context: Context?) : super(context) {
        this.afterChildrenLayout = afterChildrenLayout
    }

    constructor(
        afterChildrenLayout: Runnable,
        context: Context?,
        @Orientation orientation: Int,
        reverseLayout: Boolean,
    ) : super(context, orientation, reverseLayout) {
        this.afterChildrenLayout = afterChildrenLayout
    }

    constructor(
        afterChildrenLayout: Runnable,
        context: Context?,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.afterChildrenLayout = afterChildrenLayout
    }

    override fun onLayoutChildren(recycler: Recycler?, state: State?) {
        super.onLayoutChildren(recycler, state)
        afterChildrenLayout?.run()
    }
}
