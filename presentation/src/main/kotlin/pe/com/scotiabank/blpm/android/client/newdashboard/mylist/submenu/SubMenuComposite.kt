package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.submenu

import android.content.res.Resources
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.ComposerOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class SubMenuComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val textButtonComposer: ComposerOfTextButton,
): DispatcherProvider by dispatcherProvider {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    private val paddingEntityForTextButton: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = R.dimen.canvascore_margin_12,
            bottom = R.dimen.canvascore_margin_12,
        )
    }

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val textButtonCompound = textButtonComposer.composeUiData(paddingEntityForTextButton)

        return listOf(textButtonCompound)
    }

    class Factory (
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
    ) {

        fun create(receiver: InstanceReceiver): SubMenuComposite = SubMenuComposite(
            dispatcherProvider = dispatcherProvider,
            textButtonComposer = createTextComposer(receiver)
        )

        private fun createTextComposer(receiver: InstanceReceiver) = ComposerOfTextButton(
            collector = SubMenuCollector(weakResources),
            receiver = receiver,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}