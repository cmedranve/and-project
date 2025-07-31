package pe.com.scotiabank.blpm.android.client.cardsettings.analytics.list

import androidx.core.util.Consumer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.CardSettingsFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingsConstants
import pe.com.scotiabank.blpm.android.client.cardsettings.hub.AtmCardGroup
import pe.com.scotiabank.blpm.android.client.cardsettings.hub.Card
import pe.com.scotiabank.blpm.android.client.cardsettings.hub.CardSettingHub
import pe.com.scotiabank.blpm.android.client.model.CardDetailModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.Util
import pe.com.scotiabank.blpm.android.client.util.consumer.tryAccepting

class CardSettingsAnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: CardSettingsFactory,
) : Consumer<AnalyticEventData<*>> {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterScreenViewEvent),
            InstanceHandler(::handleScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(::filterInClickEventForCard),
            InstanceHandler(::handleClickEventForCard)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(::filterInClickEventForOtherOptions),
            InstanceHandler(::handleClickEventForOtherOptions)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    override fun accept(value: AnalyticEventData<*>) {
        tryAccepting(value, selfReceiver, FirebaseCrashlytics.getInstance()::recordException)
    }

    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        val cardSettingHub: CardSettingHub = eventData.data[Constant.DATA] as? CardSettingHub
            ?: return

        val allCards: List<Card> = cardSettingHub.groups.flatMap(::toCard)
        val debitCards: List<Card> = allCards.filter(::isDebitCard)
        val creditCards: List<Card> = allCards.filter(::isCreditCard)

        val event: AnalyticsEvent = analyticFactory.whenLoadScreen(
            productsNumber = allCards.size.toString(),
            questionOne = debitCards.size.toString(),
            questionTwo = creditCards.size.toString(),
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun toCard(group: AtmCardGroup): List<Card> = group.cards

    private fun isDebitCard(
        card: Card,
    ): Boolean = AtmCardType.DEBIT == card.cardType

    private fun isCreditCard(
        card: Card,
    ): Boolean = AtmCardType.CREDIT == card.cardType

    private fun filterInClickEventForCard(eventData: AnalyticEventData<*>): Boolean {
        val isClickEvent: Boolean = AnalyticEvent.CLICK == eventData.event
        if (isClickEvent.not()) return false

        return eventData.data[CardSettingsConstants.CARD_SETTINGS_DETAIL] is CardDetailModel
    }

    private fun handleClickEventForCard(eventData: AnalyticEventData<*>) {
        val label: String = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val cardDetail: CardDetailModel = eventData.data[CardSettingsConstants.CARD_SETTINGS_DETAIL] as? CardDetailModel
            ?: return

        val cardType: AtmCardType = AtmCardType.identifyBy(cardDetail.cardType)

        val event: AnalyticsEvent = analyticFactory.whenClickEvent(
            label = label,
            productType = cardType.analyticsValue,
            accountType = getAccountType(cardDetail),
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun getAccountType(cardDetail: CardDetailModel): String {
        val accountType: String = cardDetail.brandName + Constant.HYPHEN_STRING + cardDetail.classifier
        return Util.removeAccents(accountType).lowercase()
    }

    private fun filterInClickEventForOtherOptions(eventData: AnalyticEventData<*>): Boolean {
        val isClickEvent: Boolean = AnalyticEvent.CLICK == eventData.event
        if (isClickEvent.not()) return false

        return eventData.data[CardSettingsConstants.CARD_SETTINGS_DETAIL] !is CardDetailModel
    }

    private fun handleClickEventForOtherOptions(eventData: AnalyticEventData<*>) {
        val label: String = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickEvent(
            label = label,
            productType = AnalyticsConstant.HYPHEN_STRING,
            accountType = AnalyticsConstant.HYPHEN_STRING,
        )
        analyticsDataGateway.sendEventV2(event)
    }
}
