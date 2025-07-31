package pe.com.scotiabank.blpm.android.client.atmcardhub.business

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapperImpl
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.verification.FactoryOfChannelRegistry
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.BusinessOtpApiService
import pe.com.scotiabank.blpm.android.data.net.RestBusinessCardsApiService
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class EnabledCoordinatorFactory(
    private val hub: Hub,
    private val titleText: String,
    private val retrofit: Retrofit,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create() = EnabledCoordinator(
        hub = hub,
        titleText = titleText,
        appModel = hub.appModel,
        weakResources = hub.weakResources,
        weakAppContext = hub.weakAppContext,
        factoryOfChannelRegistry = FactoryOfChannelRegistry(),
        credentialDataMapper = CredentialDataMapperImpl(),
        cardRepository = createBusinessCardRepository(),
        otpRepository = createBusinessOtpRepository(),
        weakParent = weakParent,
        scope = parentScope.newChildScope(),
        dispatcherProvider = hub.dispatcherProvider,
        mutableLiveHolder = hub.mutableLiveHolder,
        userInterface = hub.userInterface,
        uiStateHolder = DelegateUiStateHolder(),
    )

    private fun createBusinessCardRepository(): BusinessCardsRepository {
        val api: RestBusinessCardsApiService = retrofit.create(RestBusinessCardsApiService::class.java)
        return BusinessCardsRepository(api, hub.objectMapper)
    }

    private fun createBusinessOtpRepository(): BusinessOtpRepository {
        val api: BusinessOtpApiService = retrofit.create(BusinessOtpApiService::class.java)
        return BusinessOtpRepository(api, hub.objectMapper)
    }
}
