package pe.com.scotiabank.blpm.android.ui.list.items.footer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.lang.ref.WeakReference

class PageMeter(rvMainItems: RecyclerView) : Runnable {

    private val weakRvMainItems: WeakReference<RecyclerView?> = WeakReference(rvMainItems)

    override fun run() {
        weakRvMainItems.get()?.let(::updateSpaceHeightOfMainFooter)
    }

    private fun updateSpaceHeightOfMainFooter(rvMainItems: RecyclerView) {
        val srlMain: View = rvMainItems.parent as? SwipeRefreshLayout ?: return
        val preComputed: Int = srlMain.height
        SpaceMeasurement.safelyComputeThenUpdateSpaceHeight(preComputed, rvMainItems)
    }
}
