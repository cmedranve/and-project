package pe.com.scotiabank.blpm.android.ui.list.layoutmanager

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

/**
 * SafeFlexboxLayoutManager let us prevent the following runtime error:
 *
 * java.lang.ClassCastException: androidx.recyclerview.widget.RecyclerView$LayoutParams
 * cannot be cast to com.google.android.flexbox.FlexItem
 *
 * For reader, this crash comes when you use multiple recycler view, and parent recycler view
 * configure with FlexboxLayoutManager.
 *
 * Further details at:
 *
 * https://github.com/google/flexbox-layout/issues/568
 * https://stackoverflow.com/questions/56759960/crash-when-dynamically-change-layoutmanager-to-flexboxlayoutmanager
 *
 * */
class SafeFlexboxLayoutManager : FlexboxLayoutManager {

    constructor(context: Context) : super(context)

    constructor(context: Context, flexDirection: Int) : super(context, flexDirection)

    constructor(context: Context, flexDirection: Int, flexWrap: Int) : super(context, flexDirection, flexWrap)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun generateLayoutParams(
        lp: ViewGroup.LayoutParams,
    ): RecyclerView.LayoutParams = LayoutParams(lp)

    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        if (isAttachedToWindow.not()) return

        super.onAdapterChanged(oldAdapter, newAdapter)
    }
}
