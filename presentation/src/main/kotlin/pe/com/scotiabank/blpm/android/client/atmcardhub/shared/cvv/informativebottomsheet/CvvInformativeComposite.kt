package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.informativebottomsheet

import android.content.res.Resources
import com.scotiabank.canvascore.R
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.twocolumntext.FactoryOfTwoColumnEntity
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.ComposerOfTwoColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class CvvInformativeComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfOneColumnText: ComposerOfOneColumnText,
    private val composerOfTwoColumnText: ComposerOfTwoColumnText,
    private val paddingEntityOfItem: UiEntityOfPadding,
) : Composite, DispatcherProvider by dispatcherProvider {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val descriptionCompound = composerOfOneColumnText.composeUiData()

        val itemsCompound = composerOfTwoColumnText.composeUiData(paddingEntityOfItem)

        return listOf(descriptionCompound, itemsCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
    ) {

        private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity by lazy {
            FactoryOfOneColumnTextEntity()
        }

        private val factoryOfTwoColumnEntity: FactoryOfTwoColumnEntity by lazy {
            FactoryOfTwoColumnEntity()
        }

        private val paddingEntityOfDescription: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = R.dimen.canvascore_margin_24,
                bottom = R.dimen.canvascore_margin_18,
            )
        }

        private val paddingEntityOfItem: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = R.dimen.canvascore_margin_12,
            )
        }

        fun create() = CvvInformativeComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfOneColumnText = createComposerOfOneColumnText(),
            composerOfTwoColumnText = createComposerOfTwoColumnText(),
            paddingEntityOfItem = paddingEntityOfItem,
        )

        private fun createComposerOfOneColumnText(): ComposerOfOneColumnText {
            val collector = CollectorOfOneColumnText(
                weakResources = weakResources,
                factory = factoryOfOneColumnTextEntity,
                paddingEntity = paddingEntityOfDescription
            )
            return ComposerOfOneColumnText(collector)
        }

        private fun createComposerOfTwoColumnText(): ComposerOfTwoColumnText {
            val collector = CollectorOfTwoColumnText(
                weakResources = weakResources,
                factory = factoryOfTwoColumnEntity,
            )
            return ComposerOfTwoColumnText(collector)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0

    }
}
