package pe.com.scotiabank.blpm.android.ui.list.items.widthparam

import android.view.View
import androidx.core.view.doOnAttach
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.google.android.flexbox.FlexboxLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.LayoutParamsUpdaterForNested

object UiBinderOfWidthParam {

    val FLEX_GROW_AT_ONE: Float
        get() = 1.0f
    val NON_EXISTENT_FLEX_GROW: Float
        get() = 0.0f

    @JvmStatic
    fun bind(
        child: View,
        expectedFlexGrow: Float = NON_EXISTENT_FLEX_GROW,
        layoutParamsUpdatersForNested: List<LayoutParamsUpdaterForNested> = emptyList(),
    ) = child.doOnAttach {

        val recyclerView: RecyclerView = child.parent as? RecyclerView ?: return@doOnAttach
        val params: LayoutParams = child.layoutParams as? LayoutParams ?: return@doOnAttach

        val layoutManager: LayoutManager? = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            bindWidthManagedByLinear(child, params, layoutManager, layoutParamsUpdatersForNested)
            return@doOnAttach
        }
        if (layoutManager is FlexboxLayoutManager) {
            bindWidthManagedByFlexbox(
                child = child,
                params = params,
                layoutManager = layoutManager,
                expectedFlexGrow = expectedFlexGrow,
                layoutParamsUpdatersForNested = layoutParamsUpdatersForNested,
            )
        }
    }

    @JvmStatic
    private fun bindWidthManagedByLinear(
        child: View,
        params: LayoutParams,
        layoutManager: LinearLayoutManager,
        layoutParamsUpdatersForNested: List<LayoutParamsUpdaterForNested>,
    ) {
        @Orientation val orientation: Int = layoutManager.orientation

        if (isVerticalWithWrongWidthParam(orientation, params)) {
            child.updateLayoutParams(::withWidthMatchingParent)
            layoutParamsUpdatersForNested.forEach(::updateParamsOnVertical)
            return
        }

        if (isHorizontalWithWrongWidthParam(orientation, params)) {
            child.updateLayoutParams(::withWidthWrappingContent)
            layoutParamsUpdatersForNested.forEach(::updateParamsOnHorizontal)
        }
    }

    @JvmStatic
    private fun isVerticalWithWrongWidthParam(
        @Orientation orientation: Int,
        params: LayoutParams,
    ): Boolean = RecyclerView.VERTICAL == orientation && LayoutParams.MATCH_PARENT != params.width

    @JvmStatic
    private fun withWidthMatchingParent(params: LayoutParams) {
        params.width = LayoutParams.MATCH_PARENT
    }

    @JvmStatic
    private fun updateParamsOnVertical(updater: LayoutParamsUpdaterForNested) {
        updater.updateParamsOnVertical()
    }

    @JvmStatic
    private fun isHorizontalWithWrongWidthParam(
        @Orientation orientation: Int,
        params: LayoutParams,
    ): Boolean = RecyclerView.HORIZONTAL == orientation && LayoutParams.WRAP_CONTENT != params.width

    @JvmStatic
    private fun withWidthWrappingContent(params: LayoutParams) {
        params.width = LayoutParams.WRAP_CONTENT
    }

    @JvmStatic
    private fun updateParamsOnHorizontal(updater: LayoutParamsUpdaterForNested) {
        updater.updateParamsOnHorizontal()
    }

    @JvmStatic
    fun bindWidthManagedByFlexbox(
        child: View,
        params: LayoutParams,
        layoutManager: FlexboxLayoutManager,
        expectedFlexGrow: Float,
        layoutParamsUpdatersForNested: List<LayoutParamsUpdaterForNested>,
    ) {
        val flexboxParams: FlexboxLayoutParams = params as? FlexboxLayoutParams ?: return

        val isExpected: Boolean = isExpectedWidth(flexboxParams) && isExpectedFlexGrow(flexboxParams, expectedFlexGrow)
        if (isExpected) return

        child.updateLayoutParams<FlexboxLayoutParams> {
            width = FlexboxLayoutParams.WRAP_CONTENT
            flexGrow = expectedFlexGrow
        }
        layoutParamsUpdatersForNested.forEach(::updateParamsOnHorizontal)
    }

    @JvmStatic
    private fun isExpectedWidth(
        flexboxParams: FlexboxLayoutParams,
    ): Boolean = FlexboxLayoutParams.WRAP_CONTENT == flexboxParams.width

    @JvmStatic
    private fun isExpectedFlexGrow(
        flexboxParams: FlexboxLayoutParams,
        expectedFlexGrow: Float,
    ): Boolean = expectedFlexGrow == flexboxParams.flexGrow
}
