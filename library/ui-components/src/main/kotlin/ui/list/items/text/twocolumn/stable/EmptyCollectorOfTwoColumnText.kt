package pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.stable

import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText

object EmptyCollectorOfTwoColumnText: CollectorOfTwoColumnText {

    override fun collect(): List<UiEntityOfTwoColumnText> = emptyList()

}
