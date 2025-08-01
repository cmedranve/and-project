package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.date.DateFormatter
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val paddingEntityForImage: UiEntityOfPadding,
    private val imageComposerForEmptyState: ComposerOfOneColumnImage,
    private val textComposerForEmptyState: ComposerOfOneColumnText,
    private val textComposerForSuccessState: ComposerOfOneColumnText,
    private val composerOfCheckableOperation: ComposerOfCheckableOperation,
    override var currentState: UiState = UiState.BLANK,
): DispatcherProvider by dispatcherProvider,
    RecentOperationService by composerOfCheckableOperation,
    UiStateHolder
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()
    val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val imageCompoundForEmptyState = imageComposerForEmptyState.composeUiData(
            paddingEntity = paddingEntityForImage,
            visibilitySupplier = Supplier(::isEmptyVisible),
        )

        val textCompoundForEmptyState = textComposerForEmptyState.composeUiData(
            visibilitySupplier = Supplier(::isEmptyVisible),
        )

        val textCompoundForSuccessState = textComposerForSuccessState.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible)
        )

        val checkableOperationCompound = composerOfCheckableOperation.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )

        return listOf(
            imageCompoundForEmptyState,
            textCompoundForEmptyState,
            textCompoundForSuccessState,
            checkableOperationCompound,
        )
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val weakResources: WeakReference<Resources?>,
        private val currencyAmountFormatter: CurrencyFormatter,
        private val dateFormatter: DateFormatter,
    ) {

        private val dividerPositions: List<Int> by lazy {
            val firstIndex = 0
            listOf(firstIndex)
        }

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

        private val imagePaddingEntityForEmptyState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_40,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        fun create(receiver: InstanceReceiver): MainTopComposite {

            val imageCollectorForEmptyState = SingleCollectorOfOneColumnImage(R.drawable.ic_documents_my_list)
            val textCollectorForEmptyState = TextCollectorForEmptyState(horizontalPaddingEntity, weakResources)
            val textCollectorForSuccessState = TextCollectorForSuccessState(horizontalPaddingEntity, weakResources)

            return MainTopComposite (
                dispatcherProvider = dispatcherProvider,
                paddingEntityForImage = imagePaddingEntityForEmptyState,
                imageComposerForEmptyState = ComposerOfOneColumnImage(imageCollectorForEmptyState, receiver),
                textComposerForEmptyState = ComposerOfOneColumnText(textCollectorForEmptyState),
                textComposerForSuccessState = ComposerOfOneColumnText(textCollectorForSuccessState),
                composerOfCheckableOperation = createComposerOfCheckableOperation(receiver),
            )
        }

        private fun createComposerOfCheckableOperation(
            receiver: InstanceReceiver,
        ) = ComposerOfCheckableOperation(
            weakResources = weakResources,
            dividerPositions = dividerPositions,
            paddingEntity = paddingEntity,
            currencyAmountFormatter = currencyAmountFormatter,
            dateFormatter = dateFormatter,
            receiver = receiver,
        )
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
