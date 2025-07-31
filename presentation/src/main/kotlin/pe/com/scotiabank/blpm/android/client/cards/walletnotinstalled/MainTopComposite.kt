package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled

import android.content.res.Resources
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val paddingEntityForImage: UiEntityOfPadding,
    private val composerOfOneColumnImage: ComposerOfOneColumnImage,
    private val composerOfOneColumnText: ComposerOfOneColumnText,
): DispatcherProvider by dispatcherProvider {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {
        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val oneColumnImageCompound = composerOfOneColumnImage.composeUiData(
            paddingEntity = paddingEntityForImage,
        )

        val oneColumnTextCompound = composerOfOneColumnText.composeUiData()

        return listOf(oneColumnImageCompound, oneColumnTextCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            )
        }

        private val paddingEntityForImage: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = R.dimen.margin_120,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
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

        private fun createComposerOfOneColumnImage() = ComposerOfOneColumnImage(
            collector = SingleCollectorOfOneColumnImage(R.drawable.ic_google_wallet_100),
        )

        private fun createComposerOfOneColumnText() = ComposerOfOneColumnText(
            collector = CollectorOfOneColumnText(weakResources, horizontalPaddingEntity)
        )
    }

    companion object {
        private val SINGLE_KEY: Int
            get() = 0
    }
}
