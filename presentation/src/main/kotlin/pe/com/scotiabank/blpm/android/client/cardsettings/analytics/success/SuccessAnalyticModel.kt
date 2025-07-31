package pe.com.scotiabank.blpm.android.client.cardsettings.analytics.success

import androidx.core.util.Consumer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.ChangesSavedAnalyticsParams
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.success.SuccessAnalyticsParams
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.success.SuccessFactory
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingsConstants
import pe.com.scotiabank.blpm.android.client.cardsettings.detail.EditedCard
import pe.com.scotiabank.blpm.android.client.model.CardDetailModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.Util
import pe.com.scotiabank.blpm.android.client.util.consumer.tryAccepting

class SuccessAnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: SuccessFactory,
) : Consumer<AnalyticEventData<*>> {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterScreenViewEvent),
            InstanceHandler(::handleScreenEvent)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    override fun accept(value: AnalyticEventData<*>) {
        tryAccepting(value, selfReceiver, FirebaseCrashlytics.getInstance()::recordException)
    }

    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        val editedCard: EditedCard = eventData.data[CardSettingsConstants.EDITED_CARD] as? EditedCard
            ?: return

        val cardType: AtmCardType = AtmCardType.identifyBy(editedCard.cardDetail.cardType)

        val successAnalyticsParams = SuccessAnalyticsParams(
            accountType = getAccountType(editedCard.cardDetail),
            cardType = cardType.id.lowercase(),
            productType = cardType.analyticsValue,
            questionEight = editedCard.walletState,
            subProcessType = editedCard.cardDetail.ownerType.analyticLabel,
        )

        val changesSavedAnalyticsParams = ChangesSavedAnalyticsParams(
            questionOne = editedCard.isCashDispositionEnabled,
            questionThree = editedCard.isTemporarilyLockChecked,
            questionFour = editedCard.isOnlineShoppingChecked,
            questionFive = editedCard.isPurchasesAbroadChecked,
            questionSix = editedCard.isAtmWithdrawalsEnabled,
            questionSeven = editedCard.isOverdraftChecked,
        )

        val event: AnalyticsEvent = analyticFactory.whenLoadScreen(
            successAnalyticsParams = successAnalyticsParams,
            changesSavedAnalyticsParams = changesSavedAnalyticsParams
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun getAccountType(cardDetail: CardDetailModel): String {
        val accountType: String = cardDetail.brandName + Constant.HYPHEN_STRING + cardDetail.classifier
        return Util.removeAccents(accountType).lowercase()
    }
}
