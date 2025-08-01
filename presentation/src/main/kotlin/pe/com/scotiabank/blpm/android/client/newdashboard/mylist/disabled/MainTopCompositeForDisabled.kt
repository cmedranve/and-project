package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.disabled

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.onecolumn.TextCollectorForDisabledOrErrorState
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopCompositeForDisabled private constructor(
    dispatcherProvider: DispatcherProvider,
    private val imagePaddingEntity: UiEntityOfPadding,
    private val imageComposer: ComposerOfOneColumnImage,
    private val textComposer: ComposerOfOneColumnText,
    override var currentState: UiState = UiState.BLANK,
): DispatcherProvider by dispatcherProvider, UiStateHolder {

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

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

        fun create(receiver: InstanceReceiver): MainTopCompositeForDisabled {

            val imageCollector = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)
            val textCollector = createTextCollectorForDisabledOrErrorState(
                titleRes = R.string.my_list_disabled_list_title,
                descriptionRes = R.string.my_list_disabled_list_description,
            )

            return MainTopCompositeForDisabled(
                dispatcherProvider = dispatcherProvider,
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