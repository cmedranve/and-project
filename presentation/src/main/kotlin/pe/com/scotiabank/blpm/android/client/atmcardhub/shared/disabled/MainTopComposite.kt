package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.disabled

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.TextCollectorForDisabledOrErrorState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
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
    uiStateHolder: UiStateHolder,
    private val imagePaddingEntity: UiEntityOfPadding,
    private val imageComposer: ComposerOfOneColumnImage,
    private val textComposer: ComposerOfOneColumnText,
): Composite, DispatcherProvider by dispatcherProvider, UiStateHolder by uiStateHolder {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val imageCompound = imageComposer.composeUiData(
            paddingEntity = imagePaddingEntity,
            visibilitySupplier = Supplier(::isDisabledVisible),
        )

        val textCompound = textComposer.composeUiData(
            visibilitySupplier = Supplier(::isDisabledVisible),
        )

        return listOf(
            imageCompound,
            textCompound,
        )
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
        private val uiStateHolder: UiStateHolder,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            )
        }

        private val paddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val imagePaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_48,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        fun create(receiver: InstanceReceiver): MainTopComposite {

            val imageCollector = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)
            val textCollector = createTextCollectorForDisabledOrErrorState(
                titleRes = R.string.error_hub_title,
                descriptionRes = R.string.error_hub_info,
            )

            return MainTopComposite(
                dispatcherProvider = dispatcherProvider,
                uiStateHolder = uiStateHolder,
                imagePaddingEntity = imagePaddingEntity,
                imageComposer = ComposerOfOneColumnImage(imageCollector, receiver),
                textComposer = ComposerOfOneColumnText(textCollector),
            )
        }

        private fun createTextCollectorForDisabledOrErrorState(
            @StringRes titleRes: Int,
            @StringRes descriptionRes: Int,
        ) = TextCollectorForDisabledOrErrorState(
            paddingEntity = paddingEntity,
            weakResources = weakResources,
            titleRes = titleRes,
            descriptionRes = descriptionRes,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
