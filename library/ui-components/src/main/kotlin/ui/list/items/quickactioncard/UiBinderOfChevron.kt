package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import com.scotiabank.canvascore.cards.QuickActionCard

internal object UiBinderOfChevron {

    @JvmStatic
    fun attemptBind(entity: UiEntityOfChevron?, qac: QuickActionCard) {
        if (entity == null) return

        qac.setChevronIcon(entity.iconRes)
        if (entity.show) qac.showChevron() else qac.hideChevron()
    }
}
