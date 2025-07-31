package pe.com.scotiabank.blpm.android.client.base.errorstate

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.loading.ComposerOfLoading
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText

class CompositeOfErrorState(
    private val imagePaddingEntity: UiEntityOfPadding,
    private val imageComposer: ComposerOfOneColumnImage,
    private val textComposer: ComposerOfOneColumnText,
    private val loadingComposer: ComposerOfLoading,
    private val visibilitySupplier: Supplier<Boolean>,
    subStateHolder: UiErrorSubStateHolder = DelegateUiErrorSubStateHolder(visibilitySupplier),
) : UiErrorSubStateHolder by subStateHolder {

    fun compose(): List<UiCompound<*>> {

        val imageCompound = imageComposer.composeUiData(
            paddingEntity = imagePaddingEntity,
            visibilitySupplier = visibilitySupplier,
        )

        val textCompound = textComposer.composeUiData(
            visibilitySupplier = visibilitySupplier,
        )

        val loadingCompound = loadingComposer.composeUiData(
            visibilitySupplier = Supplier(::isErrorLoadingVisible),
        )

        return listOf(
            imageCompound,
            textCompound,
            loadingCompound,
        )
    }
}
