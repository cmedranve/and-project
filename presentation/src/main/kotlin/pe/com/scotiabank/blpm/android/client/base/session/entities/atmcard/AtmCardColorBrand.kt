package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import androidx.annotation.DrawableRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R

enum class AtmCardColorBrand(
    val id: Long,
    @DrawableRes val thumbnailIcon: Int,
    val nameFromNetworkCall: String,
) {
    RED_VISA(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_visa_filled_red_71,
        nameFromNetworkCall = "RED_VISA",
    ),
    RED_MASTERCARD(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_matercard_filled_red_71,
        nameFromNetworkCall = "RED_MASTERCARD",
    ),
    GOLD_VISA(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_visa_filled_gold,
        nameFromNetworkCall = "GOLD_VISA",
    ),
    GOLD_MASTERCARD(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_mastercard_filled_gold,
        nameFromNetworkCall = "GOLD_MASTERCARD",
    ),
    SILVER_VISA(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_visa_filled_silver,
        nameFromNetworkCall = "SILVER_VISA",
    ),
    SILVER_MASTERCARD(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_mastercard_filled_silver,
        nameFromNetworkCall = "SILVER_MASTERCARD",
    ),
    BLACK_VISA(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_visa_filled_black,
        nameFromNetworkCall = "BLACK_VISA",
    ),
    BLACK_MASTERCARD(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_mastercard_filled_black,
        nameFromNetworkCall = "BLACK_MASTERCARD",
    ),
    BLUE_VISA(
        id = randomLong(),
        thumbnailIcon = R.drawable.ic_card_visa_filled_blue,
        nameFromNetworkCall = "BLUE_VISA",
    );

    companion object {

        @JvmStatic
        fun getCardColorBrandByName(name: String?): AtmCardColorBrand = when (name?.uppercase()) {
            RED_VISA.nameFromNetworkCall -> RED_VISA
            RED_MASTERCARD.nameFromNetworkCall -> RED_MASTERCARD
            GOLD_VISA.nameFromNetworkCall -> GOLD_VISA
            GOLD_MASTERCARD.nameFromNetworkCall -> GOLD_MASTERCARD
            SILVER_VISA.nameFromNetworkCall -> SILVER_VISA
            SILVER_MASTERCARD.nameFromNetworkCall -> SILVER_MASTERCARD
            BLACK_VISA.nameFromNetworkCall -> BLACK_VISA
            BLACK_MASTERCARD.nameFromNetworkCall -> BLACK_MASTERCARD
            BLUE_VISA.nameFromNetworkCall -> BLUE_VISA
            else -> RED_VISA
        }
    }
}
