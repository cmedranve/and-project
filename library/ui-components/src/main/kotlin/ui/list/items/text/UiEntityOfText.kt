package pe.com.scotiabank.blpm.android.ui.list.items.text

import android.text.TextUtils
import android.text.method.MovementMethod
import androidx.annotation.StyleRes
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfText(
    @StyleRes val appearance: Int,
    val gravity: Int,
    text: CharSequence,
    val maxLines: Int = Int.MAX_VALUE,
    val whereToEllipsize: TextUtils.TruncateAt? = null,
    val movementMethod: MovementMethod? = null,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfText>,
    ChangingState by changingState,
    Recycling by recycling
{

    private var observableText: String by Delegates.observable(
        text.toString(),
        ::onChangeOfNonEntityProperty
    )

    var text: CharSequence = text
        set(value) {
            observableText = value.toString()
            field = value
        }

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfText
    ): Boolean = isUnmodified
            && appearance == other.appearance
            && gravity == other.gravity
            && text.contentEquals(other.text)
            && maxLines == other.maxLines
            && whereToEllipsize == other.whereToEllipsize
}
