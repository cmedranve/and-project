package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable

import com.google.android.gms.common.util.BiConsumer

abstract class ControllerOfSelection<D: Any> {

    internal abstract val onCheckedChange: BiConsumer<UiEntityOfCheckable<D>, Boolean>
}
