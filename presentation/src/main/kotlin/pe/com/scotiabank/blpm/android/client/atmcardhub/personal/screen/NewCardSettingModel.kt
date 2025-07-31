package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.screen.CardSetting
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.screen.CardSettingsMapper
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.screen.SettingFromUser
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.cards.CardDetailEntity
import pe.com.scotiabank.blpm.android.data.entity.cardsettings.CardSettingsDetailResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.exception.createExceptionOnIllegalResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.cardsettings.CardSettingDataRepository

class NewCardSettingModel (
    dispatcherProvider: DispatcherProvider,
    private val repository: CardSettingDataRepository,
    private val mapper: CardSettingsMapper,
) : DispatcherProvider by dispatcherProvider, CardSettingModel {

    private var cardSetting: CardSetting? = null

    override val isMainOrJoinHolder: Boolean
        get() {
            val ownerType: AtmCardOwnerType = cardSetting?.ownerType ?: return false
            return when (ownerType) {
                AtmCardOwnerType.MAIN_HOLDER -> true
                AtmCardOwnerType.JOINT_HOLDER -> true
                else -> false
            }
        }

    override val isCardLocked: Boolean
        get() {
            val settingTemporaryBlocked: SettingFromUser = cardSetting
                ?.settings
                ?.firstOrNull(::isCardLockedSetting)
                ?: return false

            return settingTemporaryBlocked.isUserFlag
        }

    override val isPurchasesDisabled: Boolean
        get() {
            val settingPurchases: SettingFromUser = cardSetting
                ?.settings
                ?.firstOrNull(::isPurchasesDisabledSetting)
                ?: return false

            return settingPurchases.isMasterFlag.not() || settingPurchases.isUserFlag.not()
        }

    private fun isCardLockedSetting(
        settingFromUser: SettingFromUser,
    ): Boolean = Constant.CARD_SETTINGS_TEMPORARY_BLOCKED == settingFromUser.id

    private fun isPurchasesDisabledSetting(
        settingFromUser: SettingFromUser,
    ): Boolean = Constant.CARD_SETTINGS_PURCHASES_INTERNET == settingFromUser.id

    override suspend fun apply(input: String): Any = withContext(ioDispatcher) {
        val httpResponse: HttpResponse<*> = repository.getCardSettingDetail(input)

        when (val responseEntity: Any? = httpResponse.body) {
            is CardSettingsDetailResponseEntity -> receiveCardSetting(responseEntity)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(CardDetailEntity::class)
        }
    }

    private fun receiveCardSetting(responseEntity: CardSettingsDetailResponseEntity): CardSetting {
        val cardSetting: CardSetting = mapper.toCardSetting(responseEntity)
        this.cardSetting = cardSetting
        return cardSetting
    }
}
