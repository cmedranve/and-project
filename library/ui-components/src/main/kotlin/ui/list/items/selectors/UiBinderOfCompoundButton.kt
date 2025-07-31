package pe.com.scotiabank.blpm.android.ui.list.items.selectors

import android.widget.CompoundButton
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

object UiBinderOfCompoundButton {

    @JvmStatic
    fun bind(isEnabled: Boolean, isChecked: Boolean, button: CompoundButton) {
        bindIfDifferent(isEnabled, button::isEnabled, button::setEnabled)
        bindIfDifferent(isChecked, button::isChecked, button::setChecked)
    }
}
