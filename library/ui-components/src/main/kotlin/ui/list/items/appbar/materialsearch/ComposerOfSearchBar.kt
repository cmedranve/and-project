package pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch

import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle

class ComposerOfSearchBar(
    private val receiver: InstanceReceiver,
) : MaterialSearchController {

    private val entity: UiEntityOfMaterialSearch by lazy {
        UiEntityOfMaterialSearch(
            receiver = receiver,
        )
    }

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompoundOfSingle<UiEntityOfMaterialSearch> = UiCompoundOfSingle(entity, visibilitySupplier)

    override fun editSearch(
        hintTextForSearchBar: CharSequence,
        hintTextForSearchView: CharSequence,
        @StyleRes textAppearanceRes: Int,
    ) {
        entity.hintTextForSearchBar = hintTextForSearchBar
        entity.hintTextForSearchView = hintTextForSearchView
        entity.textAppearanceForSearchBar = textAppearanceRes
        entity.textAppearanceForSearchView = textAppearanceRes
    }

    override fun editSearchView(
        isEnabled: Boolean,
        hintText: CharSequence,
        @StyleRes textAppearanceRes: Int,
    ) {
        entity.isEnabledInputSearchView = isEnabled
        entity.hintTextForSearchView = hintText

        if (ResourcesCompat.ID_NULL == textAppearanceRes) return

        entity.textAppearanceForSearchView = textAppearanceRes
    }
}
