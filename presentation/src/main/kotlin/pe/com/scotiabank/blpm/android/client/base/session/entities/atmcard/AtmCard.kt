package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.util.Constant

class AtmCard(
    var credentialData: CredentialData,
    val type: AtmCardType,
    val subType: AtmCardSubType = AtmCardSubType.NONE,
    val number: String = Constant.EMPTY_STRING,
    val brand: AtmCardBrand = AtmCardBrand.MASTERCARD,
    val color: AtmCardColor = AtmCardColor.RED,
    val status: AtmCardStatus = AtmCardStatus.NONE,
    val uiId: Long = randomLong(),
)
