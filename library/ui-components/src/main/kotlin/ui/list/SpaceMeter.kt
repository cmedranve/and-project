package pe.com.scotiabank.blpm.android.ui.list

import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.FrameLayout
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.scotiabank.enhancements.weakreference.getEmptyWeak
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableBinding
import pe.com.scotiabank.blpm.android.ui.databinding.AppBarPortableBinding
import pe.com.scotiabank.blpm.android.ui.list.items.footer.SpaceMeasurement
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.RootLayoutManager
import java.lang.ref.WeakReference

class SpaceMeter {

    private var weakBinding: WeakReference<ActivityPortableBinding?> = getEmptyWeak()

    private val onAppBarLayoutChange: OnLayoutChangeListener by lazy {
        OnLayoutChangeListener(::onAppBarLayoutChange)
    }

    private val mainBottomMeasurement: Runnable by lazy {
        Runnable(::measureMainBottom)
    }

    private val resultBottomMeasurement: Runnable by lazy {
        Runnable(::measureResultBottom)
    }

    private val anchoredBottomMeasurement: Runnable by lazy {
        Runnable(::measureAnchoredBottom)
    }

    fun register(binding: ActivityPortableBinding) {
        weakBinding = WeakReference(binding)
        binding.appBarPortable.appBar.addOnLayoutChangeListener(onAppBarLayoutChange)
        binding.rvMainItems.layoutManager = RootLayoutManager(
            afterChildrenLayout = mainBottomMeasurement,
            context = binding.root.context,
        )
        binding.rvResultItems.layoutManager = RootLayoutManager(
            afterChildrenLayout = resultBottomMeasurement,
            context = binding.root.context,
        )
        binding.rvAnchoredBottomItems.layoutManager = RootLayoutManager(
            afterChildrenLayout = anchoredBottomMeasurement,
            context = binding.root.context,
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onAppBarLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int,
    ) {
        measureMainBottom()
    }

    private fun measureMainBottom() {
        val binding: ActivityPortableBinding = weakBinding.get() ?: return
        updateSpaceHeightOfMainFooter(binding, binding.appBarPortable)
    }

    private fun updateSpaceHeightOfMainFooter(
        binding: ActivityPortableBinding,
        appBarPortableBinding: AppBarPortableBinding,
    ) {
        val preComputed: Int = binding.root.height - appBarPortableBinding.appBar.height
        SpaceMeasurement.safelyComputeThenUpdateSpaceHeight(preComputed, binding.rvMainItems)
    }

    private fun measureResultBottom() {
        weakBinding.get()?.let(::updateSpaceHeightOfResultFooter)
    }

    private fun updateSpaceHeightOfResultFooter(binding: ActivityPortableBinding) {
        val container: FrameLayout = binding.rvResultItems
            .parent as? FrameLayout
            ?: return

        val preComputed: Int = binding.root.height - container.top
        SpaceMeasurement.safelyComputeThenUpdateSpaceHeight(preComputed, binding.rvResultItems)
    }

    private fun measureAnchoredBottom() {
        val binding: ActivityPortableBinding = weakBinding.get() ?: return
        createSpaceBetween(binding.rvMainItems, binding.rvAnchoredBottomItems)
    }

    private fun createSpaceBetween(rvMainItems: RecyclerView, rvAnchoredBottomItems: RecyclerView) {
        val heightInPixels: Int = rvAnchoredBottomItems.height
        if (rvMainItems.paddingBottom == heightInPixels) return
        rvMainItems.updatePadding(
            bottom = heightInPixels,
        )
    }

    fun unregister() {
        weakBinding.get()
            ?.appBarPortable
            ?.appBar
            ?.removeOnLayoutChangeListener(onAppBarLayoutChange)
    }
}
