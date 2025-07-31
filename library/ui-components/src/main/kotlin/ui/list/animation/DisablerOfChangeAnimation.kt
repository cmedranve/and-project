package pe.com.scotiabank.blpm.android.ui.list.animation

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

object DisablerOfChangeAnimation {

    @JvmStatic
    fun disable(recyclerView: RecyclerView) {
        val itemAnimator = recyclerView.itemAnimator as? SimpleItemAnimator
        itemAnimator?.supportsChangeAnimations = false
    }
}
