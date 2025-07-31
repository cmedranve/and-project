package pe.com.scotiabank.blpm.android.client.base.session.entities

import pe.com.scotiabank.blpm.android.client.util.Constant

class Client(
    var phone: String = Constant.EMPTY_STRING,
    var name: String = Constant.EMPTY_STRING,
    var email: String = Constant.EMPTY_STRING,
    var firstName: String = Constant.EMPTY_STRING,
    var lastName: String = Constant.EMPTY_STRING,
    var userName: String = Constant.EMPTY_STRING,
    var isVip: Boolean = false,
    var avatar: String = Constant.EMPTY_STRING,
    var isAcceptedDataProtection: Boolean = false,
    var isAcceptedFullConsent: Boolean = false,
    var bt: String = Constant.EMPTY_STRING,
    var segmentType: String = Constant.EMPTY_STRING,
    var personType: PersonType = PersonType.NATURAL_PERSON,
    var businessName: String = Constant.EMPTY_STRING,
    var isDataUpdateRequired: Boolean = false,
    var isDataContactUpdateRequired: Boolean = false,
    var originOm: String = Constant.EMPTY_STRING
) {

    val typeCreation: String
        get() = when {
            Constant.LETTER_S.equals(originOm) -> Constant.OPEN_MARKET_USER
            Constant.LETTER_N.equals(originOm) -> Constant.REGULAR_USER
            else -> Constant.EMPTY_STRING
        }

}
