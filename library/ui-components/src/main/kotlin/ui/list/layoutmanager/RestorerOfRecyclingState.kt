package pe.com.scotiabank.blpm.android.ui.list.layoutmanager

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView.LayoutManager

object RestorerOfRecyclingState {

    private const val DEFAULT_START_POSITION = 0

    @JvmStatic
    fun restore(layoutManager: LayoutManager, state: Parcelable?) {
        state?.let(layoutManager::onRestoreInstanceState)
            ?: layoutManager.scrollToPosition(DEFAULT_START_POSITION)
    }
}
