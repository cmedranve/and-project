package pe.com.scotiabank.blpm.android.client.base.bottomsheet.list

import android.view.Window
import androidx.annotation.DimenRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.bottomsheet.types.BodyListBottomSheetType
import pe.com.scotiabank.blpm.android.client.app.applySecureSurfaceOnWindow
import pe.com.scotiabank.blpm.android.client.app.clearWindowFromSecureSurface
import pe.com.scotiabank.blpm.android.ui.list.Composer
import pe.com.scotiabank.blpm.android.ui.list.animation.DisablerOfChangeAnimation
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationUtil
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.EventHandler

object ComposerOfBottomSheetList {

    @JvmOverloads
    @JvmStatic
    fun compose(
        recyclerView: RecyclerView,
        fragmentManager: FragmentManager,
        staticData: StaticDataOfBottomSheetList,
        liveCompounds: LiveData<List<UiCompound<*>>>,
        @DimenRes horizontalPaddingRes: Int = R.dimen.canvascore_margin_30,
        isScreenshotEnabled: Boolean = false,
        scrollingEventHandler: EventHandler? = null,
    ) {
        DisablerOfChangeAnimation.disable(recyclerView)
        DecorationUtil.addDividerBelowEachItem(recyclerView, R.drawable.canvascore_divider, staticData.dividerPositions)

        val viewPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(viewPool)

        val dialogFragment: BodyListBottomSheetType = BodyListBottomSheetType.newInstance(staticData.attributes)
        dialogFragment.isCancelable = staticData.isCancelable
        dialogFragment.closeButtonVisible = staticData.isCloseButtonVisible
        dialogFragment.customRecyclerView = recyclerView
        attemptSetCallbackOfPrimaryButton(staticData, dialogFragment)
        attemptSetCallbackOfSecondaryButton(staticData, dialogFragment)
        val observer: Observer<LifecycleOwner> = Observer {
            observeOnResumeState(dialogFragment, recyclerView, viewPool, liveCompounds, horizontalPaddingRes, isScreenshotEnabled, scrollingEventHandler)
        }
        dialogFragment.viewLifecycleOwnerLiveData.observe(dialogFragment, observer)
        dialogFragment.show(fragmentManager, dialogFragment::class.simpleName)
    }

    @JvmStatic
    private fun attemptSetCallbackOfPrimaryButton(
        staticData: StaticDataOfBottomSheetList,
        dialog: BodyListBottomSheetType
    ) {
        val callback: Runnable = staticData.callbackOfPrimaryButton ?: return
        dialog.primaryButtonEvent = { callback.run() }
    }

    @JvmStatic
    private fun attemptSetCallbackOfSecondaryButton(
        staticData: StaticDataOfBottomSheetList,
        dialog: BodyListBottomSheetType
    ) {
        val callback: Runnable = staticData.callbackOfSecondaryButton ?: return
        dialog.secondaryButtonEvent = { callback.run() }
    }

    @JvmStatic
    private fun observeOnResumeState(
        dialogFragment: BodyListBottomSheetType,
        recyclerView: RecyclerView,
        viewPool: RecyclerView.RecycledViewPool,
        liveCompounds: LiveData<List<UiCompound<*>>>,
        @DimenRes horizontalPaddingRes: Int,
        isScreenshotEnabled: Boolean,
        scrollingEventHandler: EventHandler?,
    ) {
        BottomSheetPaddingUtil.setUpPaddingOnLayout(recyclerView, dialogFragment.requireView(), horizontalPaddingRes)
        Composer.observeLiveCompounds(
            owner = dialogFragment.viewLifecycleOwner,
            viewPool = viewPool,
            recyclerView = recyclerView,
            liveCompounds = liveCompounds,
            scrollingEventHandler = scrollingEventHandler,
        )
        attemptEnablingScreenshot(dialogFragment, isScreenshotEnabled)
    }

    @JvmStatic
    private fun attemptEnablingScreenshot(dialogFragment: BodyListBottomSheetType, isEnabled: Boolean) {
        val window: Window = dialogFragment.dialog?.window ?: return
        if (isEnabled) window.clearWindowFromSecureSurface() else window.applySecureSurfaceOnWindow()
    }
}
