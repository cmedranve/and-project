package pe.com.scotiabank.blpm.android.client.atmcardhub.personal

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapperImpl
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.verification.FactoryOfChannelRegistry
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.NewRestGooglePayApiService
import pe.com.scotiabank.blpm.android.data.net.RestCardSettingApiService
import pe.com.scotiabank.blpm.android.data.net.RestCardSettingsDataApiService
import pe.com.scotiabank.blpm.android.data.net.RestCollectionsApiService
import pe.com.scotiabank.blpm.android.data.net.RestCreditCardApiService
import pe.com.scotiabank.blpm.android.data.net.RestDebitCardApiService
import pe.com.scotiabank.blpm.android.data.net.RestPersonalProductApiService
import pe.com.scotiabank.blpm.android.data.repository.OldCardSettingsDataRepository
import pe.com.scotiabank.blpm.android.data.repository.NewGatesDataRepository
import pe.com.scotiabank.blpm.android.data.repository.PersonalGatesDataRepository
import pe.com.scotiabank.blpm.android.data.repository.cardsettings.CardSettingDataRepository
import pe.com.scotiabank.blpm.android.data.repository.collections.CollectionsRepository
import pe.com.scotiabank.blpm.android.data.repository.creditcard.CreditCardRepository
import pe.com.scotiabank.blpm.android.data.repository.debitcard.DebitCardRepository
import pe.com.scotiabank.blpm.android.data.repository.googlepay.NewGooglePayRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.PersonalProductRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.ProductRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class EnabledCoordinatorFactory(
    private val hub: Hub,
    private val titleText: String,
    private val retrofit: Retrofit,
    private val isDeepLink: Boolean,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create() = EnabledCoordinator(
        hub = hub,
        retrofit = retrofit,
        titleText = titleText,
        isDeepLink = isDeepLink,
        appModel = hub.appModel,
        weakAppContext = hub.weakAppContext,
        factoryOfChannelRegistry = FactoryOfChannelRegistry(),
        credentialDataMapper = CredentialDataMapperImpl(),
        gatesDataRepository = createGatesDataRepository(),
        productRepository = createProductRepository(),
        debitCardRepository = createDebitCardRepository(),
        creditCardRepository = createCreditCardRepository(),
        cardSettingsDataRepository = createCardSettingsDataRepository(),
        newCardSettingsDataRepository = createNewCardSettingDataRepository(),
        googlePayRepository = createGooglePayRepository(),
        collectionsRepository = createCollectionsRepository(),
        weakParent = weakParent,
        scope = parentScope.newChildScope(),
        dispatcherProvider = hub.dispatcherProvider,
        mutableLiveHolder = hub.mutableLiveHolder,
        userInterface = hub.userInterface,
        uiStateHolder = DelegateUiStateHolder(),
    )

    private fun createGatesDataRepository(): NewGatesDataRepository {
        val api: RestPersonalProductApiService = retrofit.create(RestPersonalProductApiService::class.java)
        return PersonalGatesDataRepository(api, hub.objectMapper)
    }

    private fun createProductRepository(): ProductRepository {
        val api: RestPersonalProductApiService = retrofit.create(RestPersonalProductApiService::class.java)
        return PersonalProductRepository(api, hub.objectMapper)
    }

    private fun createDebitCardRepository(): DebitCardRepository {
        val api: RestDebitCardApiService = retrofit.create(RestDebitCardApiService::class.java)
        return DebitCardRepository(api, hub.objectMapper)
    }

    private fun createCreditCardRepository(): CreditCardRepository {
        val api: RestCreditCardApiService = retrofit.create(RestCreditCardApiService::class.java)
        return CreditCardRepository(api, hub.objectMapper)
    }

    private fun createCardSettingsDataRepository(): OldCardSettingsDataRepository {
        val api: RestCardSettingsDataApiService = retrofit.create(RestCardSettingsDataApiService::class.java)
        return OldCardSettingsDataRepository(api, hub.objectMapper)
    }

    private fun createNewCardSettingDataRepository(): CardSettingDataRepository {
        val api: RestCardSettingApiService = retrofit.create(RestCardSettingApiService::class.java)
        return CardSettingDataRepository(api, hub.objectMapper)
    }

    private fun createGooglePayRepository(): NewGooglePayRepository {
        val api: NewRestGooglePayApiService = retrofit.create(NewRestGooglePayApiService::class.java)
        return NewGooglePayRepository(api, hub.objectMapper)
    }

    private fun createCollectionsRepository(): CollectionsRepository {
        val api: RestCollectionsApiService = retrofit.create(RestCollectionsApiService::class.java)
        return CollectionsRepository(api, hub.objectMapper)
    }
}
