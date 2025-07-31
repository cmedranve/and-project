package pe.com.scotiabank.blpm.android.ui.list.items.selectors

import com.google.android.material.card.MaterialCardView
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

internal object UiBinderOfCheckableCard {

    @JvmStatic
    fun bind(isEnabled: Boolean, isChecked: Boolean, button: MaterialCardView) {
        bindIfDifferent(isEnabled, button::isEnabled, button::setEnabled)
        bindIfDifferent(isChecked, button::isChecked, button::setChecked)
    }
}
