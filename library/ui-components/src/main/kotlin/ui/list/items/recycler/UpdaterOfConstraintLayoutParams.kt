package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class UpdaterOfConstraintLayoutParams(
    nestedRecyclerView: RecyclerView,
): LayoutParamsUpdaterForNested {

    private val weakNestedRecyclerView: WeakReference<RecyclerView?> = WeakReference(nestedRecyclerView)

    override fun updateParamsOnVertical() {
        weakNestedRecyclerView.get()?.updateLayoutParams(::assignParamsOnVertical)
    }

    override fun updateParamsOnHorizontal() {
        weakNestedRecyclerView.get()?.updateLayoutParams(::assignParamsOnHorizontal)
    }

    private fun assignParamsOnVertical(layoutParams: LayoutParams) {
        layoutParams.width = LayoutParams.MATCH_CONSTRAINT
        layoutParams.matchConstraintDefaultWidth = LayoutParams.MATCH_CONSTRAINT_SPREAD
    }

    private fun assignParamsOnHorizontal(layoutParams: LayoutParams) {
        layoutParams.width = LayoutParams.WRAP_CONTENT
        layoutParams.matchConstraintDefaultWidth = LayoutParams.MATCH_CONSTRAINT_SPREAD
    }
}
