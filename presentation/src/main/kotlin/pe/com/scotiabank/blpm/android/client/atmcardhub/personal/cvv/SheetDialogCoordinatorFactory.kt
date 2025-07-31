package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.cvv

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.analytics.factories.atmcardhub.atmcard.AtmCardFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsUtil
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AnalyticModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.IdRegistry
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.Model
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.MutableStoreOfAtmCard
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.SheetDialogCoordinator
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet.CardDataComposite
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.informativebottomsheet.CvvInformativeComposite
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.cipher.Decryption
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.repository.creditcard.CreditCardRepository
import pe.com.scotiabank.blpm.android.data.repository.debitcard.DebitCardRepository
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.lang.ref.WeakReference

class SheetDialogCoordinatorFactory(
    private val hub: Hub,
    private val credentialDataMapper: CredentialDataMapper,
    private val debitCardRepository: DebitCardRepository,
    private val creditCardRepository: CreditCardRepository,
    private val parentScope: CoroutineScope,
) {

    fun create(
        atmCardInfo: AtmCardInfo,
        mutableLiveCompoundsOfSheetDialog: MutableLiveData<List<UiCompound<*>>>,
        weakParent: WeakReference<out Coordinator?>,
    ): SheetDialogCoordinator {

        val storeOfAtmCard = MutableStoreOfAtmCard()
        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        return SheetDialogCoordinator(
            factoryOfCardDataComposite = createFactoryOfCardDataComposite(uiStateHolder, idRegistry),
            factoryOfCvvInformativeComposite = createFactoryOfCvvInformativeComposite(),
            weakResources = hub.weakResources,
            mutableLiveCompoundsOfSheetDialog = mutableLiveCompoundsOfSheetDialog,
            model = createModel(atmCardInfo, storeOfAtmCard),
            analyticModel = createAnalyticModel(atmCardInfo),
            idRegistry = idRegistry,
            cardName = atmCardInfo.cardName,
            uriHolder = UriHolder(hub.weakResources),
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfCardDataComposite(
        uiStateHolder: UiStateHolder,
        idRegistry: IdRegistry,
    ) = CardDataComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        appModel = hub.appModel,
        weakAppContext = hub.weakAppContext,
        weakResources = hub.weakResources,
        credentialDataMapper = credentialDataMapper,
        uiStateHolder = uiStateHolder,
        idRegistry = idRegistry,
    )

    private fun createFactoryOfCvvInformativeComposite() = CvvInformativeComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
    )

    private fun createModel(
        atmCardInfo: AtmCardInfo,
        storeOfAtmCard: MutableStoreOfAtmCard,
    ): Model = ModelForPersonalBanking(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
        atmCardInfo = atmCardInfo,
        storeOfAtmCard = storeOfAtmCard,
        credentialDataMapper = credentialDataMapper,
        decryption = Decryption(),
        debitCardRepository = debitCardRepository,
        creditCardRepository = creditCardRepository,
        pushOtpFlowChecker = PushOtpFlowChecker(hub.appModel),
    )

    private fun createAnalyticModel(atmCardInfo: AtmCardInfo): AnalyticModel {
        val cardType = atmCardInfo.atmCard.subType
        val analyticFactory = AtmCardFactory(
            systemDataFactory = hub.systemDataFactory,
            previousSectionDetail = cardType.sectionDetailAnalyticsValue,
            productType = cardType.analyticsValue,
            accountType = AnalyticsUtil.replaceAllEmptyForHyphen(atmCardInfo.cardName)
        )

        return AnalyticModel(hub.analyticsDataGateway, analyticFactory)
    }
}
