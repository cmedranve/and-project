package pe.com.scotiabank.blpm.android.ui.list.items

import android.os.Parcelable
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.reflect.KProperty

val emptyUiEntityOfRecycler: UiEntityOfRecycler by lazy {
    UiEntityOfRecycler(
        paddingEntity = UiEntityOfPadding(),
        compoundsById = LinkedHashMap(),
        layoutManagerFactory = FactoryOfLinearLayoutManager(),
    )
}

val emptyUiEntities: List<EmptyUiEntity> by lazy {
    listOf(EmptyUiEntity)
}

object EmptyUiEntity: IdentifiableUiEntity<EmptyUiEntity> {

    override val id: Long = randomLong()

    override fun isHoldingTheSameContentAs(other: EmptyUiEntity): Boolean = true

    override val isUnmodified: Boolean by ImmutableState::isUnmodified

    override fun <E : IdentifiableUiEntity<E>> onChangeOfEntityProperty(
        property: KProperty<E?>,
        oldValue: E?,
        newValue: E?
    ) = ImmutableState.onChangeOfEntityProperty(property, oldValue, newValue)

    override fun <A : Any> onChangeOfNonEntityProperty(
        property: KProperty<A?>,
        oldValue: A?,
        newValue: A?
    ) = ImmutableState.onChangeOfNonEntityProperty(property, oldValue, newValue)

    override fun resetChangingState() = ImmutableState.resetChangingState()

    override var recyclingState: Parcelable? = StatelessRecycling.recyclingState
}
