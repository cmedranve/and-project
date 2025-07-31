package pe.com.scotiabank.blpm.android.client.base.session.entities

import pe.com.scotiabank.blpm.android.client.biometric.BiometricEnrollmentMapper.transformToBiometricConfigurationModel
import pe.com.scotiabank.blpm.android.client.model.*
import pe.com.scotiabank.blpm.android.client.base.session.exchangerate.ExchangeRateMapper
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.loop2pay.Loop2PayConstant
import pe.com.scotiabank.blpm.android.data.entity.*
import pe.com.scotiabank.blpm.android.data.entity.biometric.UserAuthenticatorEntity

object ProfileMapper {

    @JvmStatic
    fun transformProfile(profileEntity: ProfileEntity?): Profile = Profile(
        id = profileEntity?.id.orEmpty(),
        client = transformClient(profileEntity?.clientEntity),
        preferenceModel = transformPreference(profileEntity?.preferenceEntity),
        factorModels = transformFactors(profileEntity?.factorEntities.orEmpty()),
        personModels = transformAdvisor(profileEntity?.advisorEntities),
        exchangeRates = ExchangeRateMapper.transform(profileEntity?.rateEntities.orEmpty()),
        hash = profileEntity?.hash.orEmpty(),
        profileType = profileEntity?.profileType.orEmpty(),
        sectoristType = profileEntity?.sectoristType.orEmpty(),
        biometricConfigurationModel = profileEntity?.authenticators?.firstOrNull()
            ?.transformToBiometricConfigurationModel(),
        loop2payStatus = profileEntity?.eligibilityEntity?.status.orEmpty(),
        products = profileEntity?.products.orEmpty(),
        businessProducts = profileEntity?.businessProducts.orEmpty(),
        interoperability = transformInteroperability(profileEntity?.interoperability),
        directories = profileEntity?.directories.orEmpty(),
        showTokenizationCard = profileEntity?.eligibilityEntity?.showTokenizationCard.stringToBoolean(false),
        showNewFx = profileEntity?.eligibilityEntity?.showNewFx.orEmpty(),
        isPushOtpEnabled = transformPushOtp(profileEntity),
    )

    private fun transformClient(clientEntity: ClientEntity?): Client = Client().apply {
        phone = clientEntity?.phone.orEmpty()
        name = clientEntity?.name.orEmpty()
        email = clientEntity?.email.orEmpty()
        firstName = clientEntity?.firstName.orEmpty()
        lastName = clientEntity?.lastName.orEmpty()
        userName = clientEntity?.userName.orEmpty()
        isVip = clientEntity?.isVip ?: false
        personType = PersonType.identifyBy(clientEntity?.personType.orEmpty())
        avatar = clientEntity?.avatar.orEmpty()
        bt = clientEntity?.bt.orEmpty()
        segmentType = clientEntity?.segmentType.orEmpty()
        isAcceptedDataProtection = clientEntity?.isAcceptedDataProtection ?: false
        isAcceptedFullConsent = clientEntity?.isAcceptedFullConsent ?: false
        businessName = clientEntity?.businessName.orEmpty()
        isDataUpdateRequired = clientEntity?.dataUpdateRequired.stringToBoolean(false)
        isDataContactUpdateRequired = clientEntity?.dataContactUpdate.stringToBoolean(true)
        originOm = clientEntity?.originOm.orEmpty()
    }

    private fun transformPreference(
        entity: PreferenceEntity?,
    ): PreferenceModel = PreferenceModel().apply {
        userId = entity?.userId.orEmpty()
        isShowBalance = entity?.isShowBalance ?: false
    }

    private fun transformFactors(
        entities: List<FactorEntity?>
    ): List<FactorModel> = entities.filterNotNull().map(ProfileMapper::transformFactor)

    private fun transformFactor(entity: FactorEntity?): FactorModel = FactorModel().apply {
        type = entity?.type.orEmpty()
        value = entity?.value.orEmpty()
        status = entity?.status.orEmpty()
        isDefaultAuth = entity?.isDefaultAuth ?: false
    }

    private fun transformAdvisor(entity: PersonEntity?): PersonModel = PersonModel().apply {
        phone = entity?.phone
        name = entity?.name
        email = entity?.email
    }

    private fun transformInteroperability(
        interoperabilityEntity: InteroperabilityEntity?
    ): InteroperabilityModel = InteroperabilityModel(
        whiteList = interoperabilityEntity?.whiteList ?: Loop2PayConstant.WHITELIST_UNAVAILABLE,
        migration = interoperabilityEntity?.migration?.let(::isMigrationEnabled) ?: false,
    )

    private fun String?.stringToBoolean(defaultValue: Boolean): Boolean {
        if (this.isNullOrEmpty()) {
            return defaultValue
        }
        return this == Constant.LETTER_S
    }

    private fun transformPushOtp(profileEntity: ProfileEntity?): Boolean {
        val isPushOtpEnabled = profileEntity?.pushNotificationEnabled.stringToBoolean(false)
        if (isPushOtpEnabled.not()) return false

        val pushAuthenticator: UserAuthenticatorEntity.Data = profileEntity?.authenticators
            ?.find { authenticator -> Constant.PUSH_NOTIFICATION == authenticator.type }
            ?: return false

        return pushAuthenticator.enabled
    }

    private fun isMigrationEnabled(
        migration: String
    ): Boolean = Loop2PayConstant.WHITELIST_AVAILABLE.contentEquals(other = migration, ignoreCase = true)
}
