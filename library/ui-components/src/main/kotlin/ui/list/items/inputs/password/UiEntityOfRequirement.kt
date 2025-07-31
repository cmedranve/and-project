package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import androidx.core.util.Function
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfRequirement(
    val text: String,
    internal val statusIdentifier: Function<CharSequence, RequirementStatus>,
    private val contentDescriptionOnMet: String = Constant.EMPTY,
    private val contentDescriptionOnNotMet: String = Constant.EMPTY,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfRequirement>,
    ChangingState by changingState,
    Recycling by recycling
{

    internal var mutableStatus: RequirementStatus by Delegates.observable(
        RequirementStatus.DEFAULT,
        ::onChangeOfNonEntityProperty
    )
    val status: RequirementStatus
        get() = mutableStatus

    val isDefault: Boolean
        get() = RequirementStatus.DEFAULT == status

    val isSatisfied: Boolean
        get() = RequirementStatus.SATISFIED == status

    val isFailure: Boolean
        get() = RequirementStatus.FAILURE == status

    val contentDescription: String
        get() = when (status) {
            RequirementStatus.DEFAULT -> Constant.EMPTY
            RequirementStatus.SATISFIED -> contentDescriptionOnMet
            RequirementStatus.FAILURE -> contentDescriptionOnNotMet
        }

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfRequirement,
    ): Boolean = isUnmodified
            && text.contentEquals(other.text)
            && status == other.status
            && contentDescriptionOnMet.contentEquals(other.contentDescriptionOnMet)
            && contentDescriptionOnNotMet.contentEquals(other.contentDescriptionOnNotMet)

    override fun reset() {
        mutableStatus = RequirementStatus.DEFAULT
    }
}
