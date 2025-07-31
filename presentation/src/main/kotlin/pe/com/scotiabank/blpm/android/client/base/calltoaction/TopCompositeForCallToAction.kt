package pe.com.scotiabank.blpm.android.client.base.calltoaction

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class TopCompositeForCallToAction private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val visibilitySupplier: Supplier<Boolean>,
    private val paddingEntityForImage: UiEntityOfPadding,
    private val composerOfOneColumnImage: ComposerOfOneColumnImage,
    private val composerOfOneColumnText: ComposerOfOneColumnText,
) : Composite, DispatcherProvider by dispatcherProvider, UiStateHolder by uiStateHolder {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val compoundOfOneColumnImage = composerOfOneColumnImage.composeUiData(
            paddingEntity = paddingEntityForImage,
            visibilitySupplier = visibilitySupplier,
        )

        val compoundOfOneColumnText = composerOfOneColumnText.composeUiData(
            visibilitySupplier = visibilitySupplier,
        )

        return listOf(compoundOfOneColumnImage, compoundOfOneColumnText)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
        private val factory: FactoryOfOneColumnTextEntity,
        @DrawableRes private val imageRes: Int,
        @StringRes private val titleRes: Int,
        private val description: CharSequence,
        private val uiStateHolder: UiStateHolder,
    ) : Composite.Factory {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            )
        }

        private val paddingEntityForImage: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_48,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val paddingEntityForText: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        override fun create(
            receiver: InstanceReceiver,
            visibilitySupplier: Supplier<Boolean>,
        ) = TopCompositeForCallToAction(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            visibilitySupplier = visibilitySupplier,
            paddingEntityForImage = paddingEntityForImage,
            composerOfOneColumnImage = createComposerOfOneColumnImage(),
            composerOfOneColumnText = createComposerOfOneColumnText(),
        )

        private fun createComposerOfOneColumnImage() = ComposerOfOneColumnImage(
            collector = SingleCollectorOfOneColumnImage(imageRes)
        )

        private fun createComposerOfOneColumnText(): ComposerOfOneColumnText {
            val title: String = weakResources.get()?.getString(titleRes).orEmpty()

            val collector = CollectorOfOneColumnContent(
                factory = factory,
                paddingEntity = paddingEntityForText,
                title = title,
                description = description,
            )
            return ComposerOfOneColumnText(collector)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
