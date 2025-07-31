package pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown

import com.scotiabank.canvascore.selectors.Dropdown
import pe.com.scotiabank.blpm.android.ui.databinding.ViewDropdownSelectorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.attemptBindToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.bindTextIfNotBlank

object UiBinderOfDropdownSelector {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfDropdownSelector<D>, ViewDropdownSelectorItemBinding>
    ) {
        val entity: UiEntityOfDropdownSelector<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfDropdownSelector<D>,
        binding: ViewDropdownSelectorItemBinding
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindHeadings(entity, binding.ddSelector)
        bindItemGroup(entity, binding.ddSelector)
        bindError(entity, binding.ddSelector)
        attemptBindToolTip(entity.toolTipEntity, binding, binding.ddSelector::setToolTip)
        binding.ddSelector.setCallbacks(entity.dropdownCallbacks)
    }

    @JvmStatic
    private fun <D: Any> bindHeadings(
        entity: UiEntityOfDropdownSelector<D>,
        ddSelector: Dropdown,
    ) {
        ddSelector.setSelectorHint(entity.isFirstItemHint)
        bindTextIfNotBlank(entity::titleText, ddSelector::setSelectorTitle)
    }

    @JvmStatic
    private fun <D: Any> bindItemGroup(
        entity: UiEntityOfDropdownSelector<D>,
        ddSelector: Dropdown,
    ) {
        val isEmpty: Boolean = ddSelector.getSpinnerAdapter()?.isEmpty ?: true

        if (isEmpty) {
            createItems(entity.itemEntities, ddSelector)
        }
        attemptRestoreSelection(entity.controller, ddSelector)
    }

    @JvmStatic
    private fun <D: Any> createItems(
        itemEntities: List<UiEntityOfDropdownSelectorItem<D>>,
        ddSelector: Dropdown,
    ) {
        val texts: List<String> = itemEntities
            .map { itemEntity -> itemEntity.text }
        ddSelector.initialize(texts)
    }

    @JvmStatic
    private fun <D: Any> attemptRestoreSelection(
        controller: SelectionControllerOfDropdownSelector<D>,
        ddSelector: Dropdown,
    ) {
        val positionOfSelectedItem: Int = controller.positionOfSelectedItem
        if (SelectionControllerOfDropdownSelector.INVALID_POSITION == positionOfSelectedItem) return
        if (positionOfSelectedItem == ddSelector.getSelectedItemPosition()) return

        ddSelector.setSelection(positionOfSelectedItem)
    }

    @JvmStatic
    private fun <D: Any> bindError(
        entity: UiEntityOfDropdownSelector<D>,
        ddSelector: Dropdown,
    ) {
        val errorText: CharSequence = entity.errorText
        if (errorText.isBlank()) {
            ddSelector.removeError()
            return
        }
        ddSelector.setError(errorText, entity.accessibilityLabelOfError)
    }
}
