package pe.com.scotiabank.blpm.android.ui.list.items.buddytip

import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfBuddyTip {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        callback: Runnable? = null,
    ): List<UiEntityOfBuddyTip>
}
