package pe.com.scotiabank.blpm.android.ui.list

import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.scotiabank.enhancements.weakreference.getEmptyWeak
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableHostBinding
import pe.com.scotiabank.blpm.android.ui.list.items.footer.SpaceMeasurement
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.RootLayoutManager
import java.lang.ref.WeakReference

class SpaceMeterForCoordinator {

    private var weakBinding: WeakReference<ActivityPortableHostBinding?> = getEmptyWeak()

    private val onAppBarLayoutChange: OnLayoutChangeListener by lazy {
        OnLayoutChangeListener(::onAppBarLayoutChange)
    }

    private val mainBottomMeasurement: Runnable by lazy {
        Runnable(::measureMainBottom)
    }

    private val resultBottomMeasurement: Runnable by lazy {
        Runnable(::measureResultBottom)
    }

    fun register(binding: ActivityPortableHostBinding) {
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
        binding.rvAnchoredBottomItems.layoutManager = LinearLayoutManager(binding.root.context)
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
        weakBinding.get()?.let(::updateSpaceHeightOfMainFooter)
    }

    private fun updateSpaceHeightOfMainFooter(binding: ActivityPortableHostBinding) {
        val preComputed: Int = binding.srlMain.height
        SpaceMeasurement.safelyComputeThenUpdateSpaceHeight(preComputed, binding.rvMainItems)
    }

    private fun measureResultBottom() {
        weakBinding.get()?.let(::updateSpaceHeightOfResultFooter)
    }

    private fun updateSpaceHeightOfResultFooter(binding: ActivityPortableHostBinding) {
        val container: FrameLayout = binding.rvResultItems
            .parent as? FrameLayout
            ?: return

        val preComputed: Int = binding.root.height - container.top
        SpaceMeasurement.safelyComputeThenUpdateSpaceHeight(preComputed, binding.rvResultItems)
    }

    fun unregister() {
        weakBinding.get()
            ?.appBarPortable
            ?.appBar
            ?.removeOnLayoutChangeListener(onAppBarLayoutChange)
    }
}
