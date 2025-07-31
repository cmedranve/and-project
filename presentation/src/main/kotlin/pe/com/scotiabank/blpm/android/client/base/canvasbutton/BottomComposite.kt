package pe.com.scotiabank.blpm.android.client.base.canvasbutton

import androidx.core.util.Supplier
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.buttons.CanvasButton
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.ComposerOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.CanvasButtonController
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.backtype.ComposerOfNavigationButtonBack
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.backtype.NavigationButtonBackController
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.continuetype.ComposerOfNavigationButtonContinue
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.continuetype.NavigationButtonContinueController
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.submittype.ComposerOfNavigationButtonSubmit
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.submittype.NavigationButtonSubmitController
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.util.concurrent.ConcurrentHashMap

class BottomComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfCanvasButton: ComposerOfCanvasButton,
    private val visibilitySupplierForCanvasButton: Supplier<Boolean>,
    private val composerOfNavigationButtonBack: ComposerOfNavigationButtonBack,
    private val visibilitySupplierForNavigationButtonBack: Supplier<Boolean>,
    private val composerOfNavigationButtonContinue: ComposerOfNavigationButtonContinue,
    private val visibilitySupplierForNavigationButtonContinue: Supplier<Boolean>,
    private val composerOfNavigationButtonSubmit: ComposerOfNavigationButtonSubmit,
    private val visibilitySupplierForNavigationButtonSubmit: Supplier<Boolean>,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    CanvasButtonController by composerOfCanvasButton,
    NavigationButtonBackController by composerOfNavigationButtonBack,
    NavigationButtonContinueController by composerOfNavigationButtonContinue,
    NavigationButtonSubmitController by composerOfNavigationButtonSubmit
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    fun addCanvasButton(
        id: Long,
        isEnabled: Boolean,
        text: CharSequence,
        data: Any? = null,
        type: Int = CanvasButton.PRIMARY,
    ): BottomComposite = apply {

        composerOfCanvasButton.add(id, isEnabled, text, data, type)
    }

    fun addNavigationButtonBack(
        id: Long,
        isEnabled: Boolean,
        text: CharSequence,
        data: Any? = null,
    ): BottomComposite = apply {

        composerOfNavigationButtonBack.add(id, isEnabled, text, data)
    }

    fun addNavigationButtonContinue(
        id: Long,
        isEnabled: Boolean,
        text: CharSequence,
        data: Any? = null,
    ): BottomComposite = apply {

        composerOfNavigationButtonContinue.add(id, isEnabled, text, data)
    }

    fun addNavigationButtonSubmit(
        id: Long,
        isEnabled: Boolean,
        text: CharSequence,
        data: Any? = null,
    ): BottomComposite = apply {

        composerOfNavigationButtonSubmit.add(id, isEnabled, text, data)
    }

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val canvasButtonCompound = composerOfCanvasButton.composeUiData(
            visibilitySupplier = visibilitySupplierForCanvasButton,
        )

        val navigationButtonBackCompound = composerOfNavigationButtonBack.composeUiData(
            visibilitySupplier = visibilitySupplierForNavigationButtonBack,
        )

        val navigationButtonContinueCompound = composerOfNavigationButtonContinue.composeUiData(
            visibilitySupplier = visibilitySupplierForNavigationButtonContinue,
        )

        val navigationButtonSubmitCompound = composerOfNavigationButtonSubmit.composeUiData(
            visibilitySupplier = visibilitySupplierForNavigationButtonSubmit,
        )

        return listOf(
            canvasButtonCompound,
            navigationButtonBackCompound,
            navigationButtonContinueCompound,
            navigationButtonSubmitCompound,
        )
    }

    class Factory(private val dispatcherProvider: DispatcherProvider) {

        private val paddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = R.dimen.canvascore_margin_12,
                bottom = R.dimen.canvascore_margin_12,
                left = R.dimen.canvascore_margin_16,
                right = R.dimen.canvascore_margin_16,
            )
        }

        fun create(
            receiver: InstanceReceiver,
            visibilitySupplierForCanvasButton: Supplier<Boolean> = Supplier(isGoingToBeVisible()::not),
            visibilitySupplierForNavigationButtonBack: Supplier<Boolean> = Supplier(isGoingToBeVisible()::not),
            visibilitySupplierForNavigationButtonContinue: Supplier<Boolean> = Supplier(isGoingToBeVisible()::not),
            visibilitySupplierForNavigationButtonSubmit: Supplier<Boolean> = Supplier(isGoingToBeVisible()::not),
        ) = BottomComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfCanvasButton = ComposerOfCanvasButton(paddingEntity, receiver),
            visibilitySupplierForCanvasButton = visibilitySupplierForCanvasButton,
            composerOfNavigationButtonBack = ComposerOfNavigationButtonBack(paddingEntity, receiver),
            visibilitySupplierForNavigationButtonBack = visibilitySupplierForNavigationButtonBack,
            composerOfNavigationButtonContinue = ComposerOfNavigationButtonContinue(paddingEntity, receiver),
            visibilitySupplierForNavigationButtonContinue = visibilitySupplierForNavigationButtonContinue,
            composerOfNavigationButtonSubmit = ComposerOfNavigationButtonSubmit(paddingEntity, receiver),
            visibilitySupplierForNavigationButtonSubmit = visibilitySupplierForNavigationButtonSubmit,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
