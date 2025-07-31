package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import androidx.annotation.DrawableRes
import pe.com.scotiabank.blpm.android.client.R

enum class AtmCardColor(
    @DrawableRes val hubIcon: Int,
    @DrawableRes val cardBackground: Int,
    val nameFromNetworkCall: String,
) {
    RED(
        hubIcon = R.drawable.ic_card_red,
        cardBackground = R.drawable.bg_card_red,
        nameFromNetworkCall = "RED",
    ),
    GOLD(
        hubIcon = R.drawable.ic_card_gold,
        cardBackground = R.drawable.bg_card_gold,
        nameFromNetworkCall = "GOLD",
    ),
    SILVER(
        hubIcon = R.drawable.ic_card_silver,
        cardBackground = R.drawable.bg_card_silver,
        nameFromNetworkCall = "SILVER",
    ),
    BLACK(
        hubIcon = R.drawable.ic_card_black,
        cardBackground = R.drawable.bg_card_black,
        nameFromNetworkCall = "BLACK",
    ),
    BLUE(
        hubIcon = R.drawable.ic_card_blue,
        cardBackground = R.drawable.bg_card_blue,
        nameFromNetworkCall = "BLUE",
    );

    companion object {

        @JvmStatic
        fun getCardColorByName(name: String?): AtmCardColor = when (name?.uppercase()) {
            RED.nameFromNetworkCall -> RED
            GOLD.nameFromNetworkCall -> GOLD
            SILVER.nameFromNetworkCall -> SILVER
            BLACK.nameFromNetworkCall -> BLACK
            BLUE.nameFromNetworkCall -> BLUE
            else -> RED
        }
    }
}
