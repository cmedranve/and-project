package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import br.com.hst.issuergp.data.model.CardNetwork
import br.com.hst.issuergp.data.model.TokenServiceProvider
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.Constant

enum class AtmCardBrand(
    val oldNameFromNetworkCall: String,
    val newNameFromNetworkCall: String,
    @DrawableRes val digitalImage: Int,
    val analyticsValue: String,
    val cardNetwork: CardNetwork,
    val tokenServiceProvider: TokenServiceProvider,
) {

    MASTERCARD(
        oldNameFromNetworkCall = "MASTERCARD",
        newNameFromNetworkCall = "M",
        digitalImage = R.drawable.ic_mastercard_digital_card,
        analyticsValue = Constant.EMPTY_STRING,
        cardNetwork = CardNetwork.MASTERCARD,
        tokenServiceProvider = TokenServiceProvider.MASTERCARD,
    ),

    VISA(
        oldNameFromNetworkCall = "VISA",
        newNameFromNetworkCall = "V",
        digitalImage = R.drawable.ic_visa_digital_card,
        analyticsValue = Constant.EMPTY_STRING,
        cardNetwork = CardNetwork.VISA,
        tokenServiceProvider = TokenServiceProvider.VISA,
    ),

    AMERICAN_EXPRESS(
        oldNameFromNetworkCall = "AMEX",
        newNameFromNetworkCall = "A",
        digitalImage = ResourcesCompat.ID_NULL,
        analyticsValue = Constant.EMPTY_STRING,
        cardNetwork = CardNetwork.AMEX,
        tokenServiceProvider = TokenServiceProvider.AMEX,
    );

    companion object {

        @JvmStatic
        fun identifyByOldName(oldName: String?): AtmCardBrand = when (oldName?.uppercase()) {
            MASTERCARD.oldNameFromNetworkCall -> MASTERCARD
            VISA.oldNameFromNetworkCall -> VISA
            else -> MASTERCARD
        }

        @JvmStatic
        fun identifyByNewName(newName: String?): AtmCardBrand = when (newName?.uppercase()) {
            MASTERCARD.newNameFromNetworkCall -> MASTERCARD
            VISA.newNameFromNetworkCall -> VISA
            AMERICAN_EXPRESS.newNameFromNetworkCall -> AMERICAN_EXPRESS
            else -> MASTERCARD
        }
    }
}
