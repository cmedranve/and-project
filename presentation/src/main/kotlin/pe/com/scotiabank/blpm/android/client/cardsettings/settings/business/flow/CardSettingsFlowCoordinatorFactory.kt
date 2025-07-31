package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.flow

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import java.lang.ref.WeakReference

class CardSettingsFlowCoordinatorFactory(
    private val hub: Hub,
    private val businessCardRepository: BusinessCardsRepository,
    private val businessOtpRepository: BusinessOtpRepository,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(card: AtmCardInfo) = CardSettingsFlowCoordinator(
        hub = hub,
        businessCardRepository = businessCardRepository,
        businessOtpRepository = businessOtpRepository,
        card = card,
        weakParent = weakParent,
        scope = parentScope.newChildScope(),
        dispatcherProvider = hub.dispatcherProvider,
        mutableLiveHolder = hub.mutableLiveHolder,
        userInterface = hub.userInterface,
        uiStateHolder = DelegateUiStateHolder(),
    )
}