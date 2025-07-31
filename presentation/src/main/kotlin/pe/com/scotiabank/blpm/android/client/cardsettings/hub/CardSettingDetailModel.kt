package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.res.Resources
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingsModelDataMapper
import pe.com.scotiabank.blpm.android.client.model.CardDetailModel
import pe.com.scotiabank.blpm.android.client.model.CardSettingsModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.cards.CardDetailEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.exception.createExceptionOnIllegalResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.OldCardSettingsDataRepository
import java.lang.ref.WeakReference

class CardSettingDetailModel(
    dispatcherProvider: DispatcherProvider,
    private val weakResources: WeakReference<Resources?>,
    private val repository: OldCardSettingsDataRepository,
) : DispatcherProvider by dispatcherProvider, SuspendingFunction<String, Any> {

    private var cardSetting: CardDetailModel? = null

    val isMainHolder: Boolean
        get() = AtmCardOwnerType.MAIN_HOLDER == cardSetting?.ownerType

    val isCardLocked: Boolean
        get() {
            val settingTemporaryBlocked: CardSettingsModel? = cardSetting?.settings?.firstOrNull { cardSettings ->
                Constant.CARD_SETTINGS_TEMPORARY_BLOCKED == cardSettings.id
            }
            return settingTemporaryBlocked?.isUserFlag == false
        }

    val isPurchasesDisabled: Boolean
        get() {
            val settingPurchases: CardSettingsModel? = cardSetting?.settings?.firstOrNull { cardSettings ->
                Constant.CARD_SETTINGS_PURCHASES_INTERNET == cardSettings.id
            }
            if (settingPurchases?.isMasterFlag == false) return true
            return settingPurchases?.isUserFlag == false
        }

    override suspend fun apply(input: String): Any = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = repository.cardSettingsDetail(input)

        when (val responseEntity: Any? = httpResponse.body) {
            is CardDetailEntity -> receiveCardSetting(responseEntity)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(CardDetailEntity::class)
        }
    }

    private fun receiveCardSetting(cardDetailEntity: CardDetailEntity): CardDetailModel {
        val cardSetting: CardDetailModel = CardSettingsModelDataMapper.transformCardDetailEntity(
            cardDetailEntity,
            weakResources.get(),
        )
        this.cardSetting = cardSetting
        return cardSetting
    }
}
