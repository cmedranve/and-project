package pe.com.scotiabank.blpm.android.client.base.verification.business

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.base.verification.FactoryOfChannelRegistry
import pe.com.scotiabank.blpm.android.client.base.verification.IdRegistry
import pe.com.scotiabank.blpm.android.client.base.verification.MainTopComposite
import pe.com.scotiabank.blpm.android.client.base.verification.NewOtpRequestModel
import pe.com.scotiabank.blpm.android.client.base.verification.OtpVerificationCoordinator
import pe.com.scotiabank.blpm.android.client.base.verification.TransactionType
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.ChannelRegistry
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.NumberInput
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.BusinessOtpApiService
import pe.com.scotiabank.blpm.android.data.net.RestOtpApiService
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.OtpRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class BusinessOtpVerificationCoordinatorFactory(
    private val hub: Hub,
    private val factoryOfChannelRegistry: FactoryOfChannelRegistry,
    private val numberInput: NumberInput,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    private val retrofit: Retrofit
        get() = hub.appModel.sessionRetrofit

    fun create(
        titleText: String,
        transactionId: String,
        transactionType: TransactionType,
        eventOnOtpVerified: Any,
    ): OtpVerificationCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val channelRegistry: ChannelRegistry = factoryOfChannelRegistry.createFrom(hub.appModel)
        val idRegistry = IdRegistry()
        val factoryOfToolbarComposite = AppBarComposite.Factory(hub.dispatcherProvider)
        val factoryOfMainTopComposite: MainTopComposite.Factory = createFactoryOfMainTopComposite(
            uiStateHolder = uiStateHolder,
            channelRegistry = channelRegistry,
            idRegistry = idRegistry,
        )
        val otpVerificationModel: BusinessOtpVerificationModel = createOtpVerificationModel(
            transactionId = transactionId,
            transactionType = transactionType,
            eventOnOtpVerified = eventOnOtpVerified,
        )
        return OtpVerificationCoordinator(
            titleText = titleText,
            factoryOfToolbarComposite = factoryOfToolbarComposite,
            factoryOfMainTopComposite = factoryOfMainTopComposite,
            factoryOfMainBottomComposite = createFactoryOfMainBottomComposite(),
            weakResources = hub.weakResources,
            channelRegistry = channelRegistry,
            numberInput = numberInput,
            newOtpRequestModel = createNewOtpRequestModel(),
            otpVerificationModel = otpVerificationModel,
            idRegistry = idRegistry,
            availabilityRegistry = createAvailabilityRegistry(idRegistry),
            errorTextOnFieldRequired = hub.errorTextOnFieldRequired,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfMainTopComposite(
        uiStateHolder: UiStateHolder,
        channelRegistry: ChannelRegistry,
        idRegistry: IdRegistry,
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
        uiStateHolder = uiStateHolder,
        channelRegistry = channelRegistry,
        idRegistry = idRegistry,
        factory = hub.factoryOfOneColumnTextEntity,
    )

    fun createFactoryOfMainBottomComposite(): BottomComposite.Factory {
        return BottomComposite.Factory(hub.dispatcherProvider)
    }

    private fun createNewOtpRequestModel(): NewOtpRequestModel {
        val apiService: RestOtpApiService = retrofit.create(RestOtpApiService::class.java)
        val repository = OtpRepository(apiService, hub.objectMapper)
        val operation: CharArray = Constant.IDENTITY.toCharArray()
        return NewOtpRequestModel(hub.dispatcherProvider, repository, operation)
    }

    private fun createOtpVerificationModel(
        transactionId: String,
        transactionType: TransactionType,
        eventOnOtpVerified: Any,
    ): BusinessOtpVerificationModel {
        val apiService: BusinessOtpApiService = retrofit.create(BusinessOtpApiService::class.java)
        val businessOtpRepository = BusinessOtpRepository(
            apiService = apiService,
            objectMapper = hub.objectMapper,
        )
        return BusinessOtpVerificationModel(
            dispatcherProvider = hub.dispatcherProvider,
            repository = businessOtpRepository,
            transactionId = transactionId,
            transactionType = transactionType.typeForNetworkCall,
            eventOnOtpVerified = eventOnOtpVerified,
        )
    }

    private fun createAvailabilityRegistry(idRegistry: IdRegistry): AvailabilityRegistry {
        val ids: Collection<Long> = listOf(idRegistry.idOfContinueButton)
        return AvailabilityRegistry(ids)
    }
}
