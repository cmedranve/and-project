package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY

enum class AtmCardOwnerType(
    val id: Long,
    val nameFromNetworkCall: String,
    val labelFromNetworkCall: String,
    @StringRes val labelRes: Int,
    val analyticLabel: String,
) {

    MAIN_HOLDER(
        id = randomLong(),
        nameFromNetworkCall = "MAIN_HOLDER",
        labelFromNetworkCall = "MAIN_HOLDER",
        labelRes = R.string.my_cards,
        analyticLabel = "titular",
    ),

    JOINT_HOLDER(
        id = randomLong(),
        nameFromNetworkCall = "JOINT_HOLDER",
        labelFromNetworkCall = "ADDITIONAL_TO_ME",
        labelRes = R.string.my_additionals,
        analyticLabel = "adicional",
    ),

    JOINT_HOLDER_TO_OTHER(
        id = randomLong(),
        nameFromNetworkCall = "JOINT_HOLDER_TO_OTHER",
        labelFromNetworkCall = "ADDITIONAL_TO_OTHER",
        labelRes = R.string.others_additionals,
        analyticLabel = "adicional",
    ),

    NONE(
        id = randomLong(),
        nameFromNetworkCall = String.EMPTY,
        labelFromNetworkCall = String.EMPTY,
        labelRes = ResourcesCompat.ID_NULL,
        analyticLabel = String.EMPTY,
    );

    companion object {

        @JvmStatic
        fun identifyBy(ownerType: String?): AtmCardOwnerType = when (ownerType?.uppercase()) {
            MAIN_HOLDER.nameFromNetworkCall -> MAIN_HOLDER
            JOINT_HOLDER.nameFromNetworkCall -> JOINT_HOLDER
            JOINT_HOLDER_TO_OTHER.nameFromNetworkCall -> JOINT_HOLDER_TO_OTHER
            else -> NONE
        }

        @JvmStatic
        fun identifyByLabel(label: String?): AtmCardOwnerType = when (label?.uppercase()) {
            MAIN_HOLDER.labelFromNetworkCall -> MAIN_HOLDER
            JOINT_HOLDER.labelFromNetworkCall -> JOINT_HOLDER
            JOINT_HOLDER_TO_OTHER.labelFromNetworkCall -> JOINT_HOLDER_TO_OTHER
            else -> NONE
        }
    }
}
