package pe.com.scotiabank.blpm.android.ui.list.items.atmcard

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.UiEntityOfButton
import pe.com.scotiabank.blpm.android.ui.list.items.isNullableEntityTheSameAs
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfAtmCard<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    @DrawableRes backgroundDrawableRes: Int = ResourcesCompat.ID_NULL,
    @DrawableRes bankLogoRes: Int = ResourcesCompat.ID_NULL,
    cardName: CharSequence = Constant.EMPTY,
    cardNumber: CharSequence = Constant.EMPTY,
    val rightActionEntity: UiEntityOfButton<D>? = null,
    isRightActionGoingToBeVisible: Boolean = false,
    expiryDateLabel: CharSequence = Constant.EMPTY,
    expiryDateValue: CharSequence = Constant.EMPTY,
    codeLabel: CharSequence = Constant.EMPTY,
    codeValue: CharSequence = Constant.EMPTY,
    @DrawableRes brandLogoRes: Int = ResourcesCompat.ID_NULL,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity< UiEntityOfAtmCard<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var backgroundDrawableRes: Int by Delegates.observable(
        backgroundDrawableRes,
        ::onChangeOfNonEntityProperty
    )

    var bankLogoRes: Int by Delegates.observable(
        bankLogoRes,
        ::onChangeOfNonEntityProperty
    )

    private var observableCardName: String by Delegates.observable(
        cardName.toString(),
        ::onChangeOfNonEntityProperty
    )

    var cardName: CharSequence = cardName
        set(value) {
            observableCardName = value.toString()
            field = value
        }

    private var observableCardNumber: String by Delegates.observable(
        cardNumber.toString(),
        ::onChangeOfNonEntityProperty
    )

    var cardNumber: CharSequence = cardNumber
        set(value) {
            observableCardNumber = value.toString()
            field = value
        }

    var isRightActionGoingToBeVisible: Boolean by Delegates.observable(
        isRightActionGoingToBeVisible,
        ::onChangeOfNonEntityProperty
    )

    private var observableExpiryDateLabel: String by Delegates.observable(
        expiryDateLabel.toString(),
        ::onChangeOfNonEntityProperty
    )

    var expiryDateLabel: CharSequence = expiryDateLabel
        set(value) {
            observableExpiryDateLabel = value.toString()
            field = value
        }

    private var observableExpiryDateValue: String by Delegates.observable(
        expiryDateValue.toString(),
        ::onChangeOfNonEntityProperty
    )

    var expiryDateValue: CharSequence = expiryDateValue
        set(value) {
            observableExpiryDateValue = value.toString()
            field = value
        }

    private var observableCodeLabel: String by Delegates.observable(
        codeLabel.toString(),
        ::onChangeOfNonEntityProperty
    )

    var codeLabel: CharSequence = codeLabel
        set(value) {
            observableCodeLabel = value.toString()
            field = value
        }

    private var observableCodeValue: String by Delegates.observable(
        codeValue.toString(),
        ::onChangeOfNonEntityProperty
    )

    var codeValue: CharSequence = codeValue
        set(value) {
            observableCodeValue = value.toString()
            field = value
        }

    var brandLogoRes: Int by Delegates.observable(
        brandLogoRes,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfAtmCard<D>,
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && backgroundDrawableRes == other.backgroundDrawableRes
            && brandLogoRes == other.brandLogoRes
            && cardName.contentEquals(other.cardName)
            && cardNumber.contentEquals(other.cardNumber)
            && rightActionEntity.isNullableEntityTheSameAs(other.rightActionEntity)
            && isRightActionGoingToBeVisible == other.isRightActionGoingToBeVisible
            && expiryDateLabel.contentEquals(other.expiryDateLabel)
            && expiryDateValue.contentEquals(other.expiryDateValue)
            && codeLabel.contentEquals(other.codeLabel)
            && codeValue.contentEquals(other.codeValue)
            && brandLogoRes == other.brandLogoRes
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
