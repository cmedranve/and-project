package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import pe.com.scotiabank.blpm.android.ui.databinding.ViewRecyclerItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfRecycler {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfRecycler, ViewRecyclerItemBinding>) {
        val entity: UiEntityOfRecycler = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun bind(
        carrier: UiEntityCarrier<UiEntityOfRecycler, ViewRecyclerItemBinding>,
        entity: UiEntityOfRecycler,
        binding: ViewRecyclerItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        StockingOfRecycler.stockWith(carrier, entity, binding.rvItems)
    }
}
