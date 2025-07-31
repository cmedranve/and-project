package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import android.content.res.Resources
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val paddingEntityForImage: UiEntityOfPadding,
    private val composerOfOneColumnImage: ComposerOfOneColumnImage,
    private val composerOfOneColumnText: ComposerOfOneColumnText,
) : Composite, DispatcherProvider by dispatcherProvider {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()

    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val oneColumnImageCompound = composerOfOneColumnImage.composeUiData(paddingEntityForImage)

        val oneColumnTextCompound = composerOfOneColumnText.composeUiData()

        return listOf(oneColumnImageCompound, oneColumnTextCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
    ) {

        private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity by lazy {
            FactoryOfOneColumnTextEntity()
        }

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_18,
            )
        }

        private val paddingEntityForImage: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_72,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_20,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val paddingEntityForText: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        fun create() = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            paddingEntityForImage = paddingEntityForImage,
            composerOfOneColumnImage = createComposerOfOneColumnImage(),
            composerOfOneColumnText = createComposerOfOneColumnText(),
        )

        private fun createComposerOfOneColumnImage(): ComposerOfOneColumnImage {
            val collector = SingleCollectorOfOneColumnImage(R.drawable.ic_cvv_onboarding)
            return ComposerOfOneColumnImage(collector)
        }

        private fun createComposerOfOneColumnText(): ComposerOfOneColumnText {
            val collector = CollectorOfOneColumnText(
                weakResources = weakResources,
                factory = factoryOfOneColumnTextEntity,
                paddingEntity = paddingEntityForText
            )
            return ComposerOfOneColumnText(collector)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
