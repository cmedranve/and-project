package pe.com.scotiabank.blpm.android.ui.list.changingstates

import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import kotlin.reflect.KProperty

interface ChangingState {

    val isUnmodified: Boolean

    fun <E : IdentifiableUiEntity<E>> onChangeOfEntityProperty(
        property: KProperty<E?>,
        oldValue: E?,
        newValue: E?
    )

    fun <A : Any> onChangeOfNonEntityProperty(
        property: KProperty<A?>,
        oldValue: A?,
        newValue: A?
    )

    fun resetChangingState()
}
