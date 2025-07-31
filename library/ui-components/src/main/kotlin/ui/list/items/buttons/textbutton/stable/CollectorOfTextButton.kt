package pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.stable

import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton

interface CollectorOfTextButton {

    fun collect(): List<UiEntityOfTextButton<Any>>
}
