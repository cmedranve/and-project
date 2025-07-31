package pe.com.scotiabank.blpm.android.ui.list.items.buddytip

import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class EmptyCollectorOfBuddyTip: CollectorOfBuddyTip {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        callback: Runnable?,
    ): List<UiEntityOfBuddyTip> = emptyList()
}
