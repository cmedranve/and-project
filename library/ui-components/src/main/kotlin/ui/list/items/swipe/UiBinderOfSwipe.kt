package pe.com.scotiabank.blpm.android.ui.list.items.swipe

import androidx.annotation.ColorRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

object UiBinderOfSwipe {

    @JvmStatic
    fun bind(entity: UiEntityOfSwipe, swipe: SwipeRefreshLayout) {
        swipe.setOnRefreshListener(null)
        bindState(entity.state, swipe)
        bindBackgroundColor(entity.backgroundColorRes, swipe)
        bindColorSchema(entity.colorSchemaRes, swipe)
        bindRefreshing(entity, swipe)
    }

    @JvmStatic
    private fun bindState(state: SwipeState, swipe: SwipeRefreshLayout) {
        bindIfDifferent(state.isEnabled, swipe::isEnabled, swipe::setEnabled)
        bindIfDifferent(state.isRefreshing, swipe::isRefreshing, swipe::setRefreshing)
    }

    @JvmStatic
    private fun bindBackgroundColor(@ColorRes colorRes: Int, swipe: SwipeRefreshLayout) {
        swipe.setProgressBackgroundColorSchemeResource(colorRes)
    }

    @JvmStatic
    private fun bindColorSchema(@ColorRes colorRes: List<Int>, swipe: SwipeRefreshLayout) {
        swipe.setColorSchemeResources(*colorRes.toIntArray())
    }

    @JvmStatic
    private fun bindRefreshing(entity: UiEntityOfSwipe, swipe: SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            entity.state = SwipeState.REFRESHING
            entity.receiver?.receive(entity)
        }
    }
}
