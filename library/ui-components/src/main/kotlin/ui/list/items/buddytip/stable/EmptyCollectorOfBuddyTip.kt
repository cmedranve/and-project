package pe.com.scotiabank.blpm.android.ui.list.items.buddytip.stable

import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip

class EmptyCollectorOfBuddyTip: CollectorOfBuddyTip {

    override fun collect(): List<UiEntityOfBuddyTip> = emptyList()
}
