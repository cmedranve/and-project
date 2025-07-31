package pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable

import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText

object EmptyCollectorOfOneColumnText : CollectorOfOneColumnText {

    override fun collect(): List<UiEntityOfOneColumnText> = emptyList()
}
