package pe.com.scotiabank.blpm.android.ui.list.changingstates

import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import kotlin.reflect.KProperty

object ImmutableState: ChangingState {

    override var isUnmodified: Boolean = true
        private set

    override fun <E : IdentifiableUiEntity<E>> onChangeOfEntityProperty(
        property: KProperty<E?>,
        oldValue: E?,
        newValue: E?
    ) {
        // do nothing as it isn't required for immutable state
    }

    override fun <A: Any> onChangeOfNonEntityProperty(
        property: KProperty<A?>,
        oldValue: A?,
        newValue: A?
    ) {
        // do nothing as it isn't required for immutable state
    }

    override fun resetChangingState() {
        // do nothing as it isn't required for immutable state
    }
}
