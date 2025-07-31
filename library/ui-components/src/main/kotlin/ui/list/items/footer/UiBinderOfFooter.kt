package pe.com.scotiabank.blpm.android.ui.list.items.footer

import pe.com.scotiabank.blpm.android.ui.databinding.ViewFooterItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

object UiBinderOfFooter {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfFooter, ViewFooterItemBinding>) {
        val entity: UiEntityOfFooter = carrier.uiEntity ?: return

        StockingOfFooter.stockWith(carrier, entity)
    }
}
