package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent
import pe.com.scotiabank.blpm.android.ui.list.items.footer.PageMeter
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.RootLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SafeFlexboxLayoutManager

sealed interface LayoutManagerFactory {

    fun create(estimateNumberOfVisibleItems: Int, recyclerView: RecyclerView): LayoutManager
}

class FactoryOfRootLayoutManager : LayoutManagerFactory {

    override fun create(
        estimateNumberOfVisibleItems: Int,
        recyclerView: RecyclerView,
    ): LayoutManager {
        val pagerMeter = PageMeter(rvMainItems = recyclerView)
        return RootLayoutManager(afterChildrenLayout = pagerMeter, context = recyclerView.context)
    }
}

class FactoryOfLinearLayoutManager(
    @Orientation private val orientationToSet: Int = RecyclerView.VERTICAL,
) : LayoutManagerFactory {

    override fun create(
        estimateNumberOfVisibleItems: Int,
        recyclerView: RecyclerView,
    ): LayoutManager = LinearLayoutManager(recyclerView.context).apply {
        orientation = orientationToSet
        initialPrefetchItemCount = estimateNumberOfVisibleItems
    }
}

class FactoryOfFlexboxLayoutManager(
    @FlexDirection private val flexDirectionToSet: Int = FlexDirection.ROW,
    @FlexWrap private val flexWrapToSet: Int = FlexWrap.NOWRAP,
    @JustifyContent private val justifyContentToSet: Int = JustifyContent.FLEX_START,
    @AlignItems private val alignItemsToSet: Int = AlignItems.FLEX_START,
) : LayoutManagerFactory {

    override fun create(
        estimateNumberOfVisibleItems: Int,
        recyclerView: RecyclerView,
    ): LayoutManager = SafeFlexboxLayoutManager(recyclerView.context).apply {
        flexDirection = flexDirectionToSet
        flexWrap = flexWrapToSet
        justifyContent = justifyContentToSet
        alignItems = alignItemsToSet
    }
}
