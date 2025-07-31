package pe.com.scotiabank.blpm.android.client.base.session.entities

import pe.com.scotiabank.blpm.android.client.model.*
import pe.com.scotiabank.blpm.android.client.base.session.exchangerate.ExchangeRate
import pe.com.scotiabank.blpm.android.client.model.biometric.BiometricConfigurationModel
import pe.com.scotiabank.blpm.android.client.util.Constant

class Profile(
    var id: String = Constant.EMPTY_STRING,
    var client: Client? = null,
    var preferenceModel: PreferenceModel? = null,
    var factorModels: List<FactorModel> = emptyList(),
    var personModels: PersonModel? = null,
    var exchangeRates: List<ExchangeRate> = emptyList(),
    var benefitModel: BenefitModel? = null,
    var hash: String = Constant.EMPTY_STRING,
    var sectoristType: String = Constant.EMPTY_STRING,
    var isProfileUpdated: Boolean = false,
    var isAddedMyList: Boolean = false,
    var profileType: String = Constant.EMPTY_STRING,
    var goalQuantity: Int = 0,
    var loop2payStatus: String = Constant.EMPTY_STRING,
    var isShouldShowTransferOwnInCts: Boolean = false,
    var biometricConfigurationModel: BiometricConfigurationModel? = null,
    var products: List<String> = emptyList(),
    var businessProducts: List<String> = emptyList(),
    var interoperability: InteroperabilityModel? = null,
    var directories: List<String> = emptyList(),
    var showTokenizationCard: Boolean = false,
    var showNewFx: String = Constant.EMPTY_STRING,
    val isPushOtpEnabled: Boolean = false,
) {

    val isPremium: Boolean
        get() = Constant.CLIENT_ADVISOR_TYPE.equals(sectoristType, ignoreCase = true)
}
