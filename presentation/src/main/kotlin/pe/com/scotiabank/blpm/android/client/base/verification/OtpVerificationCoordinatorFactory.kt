package pe.com.scotiabank.blpm.android.client.base.verification

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.ChannelRegistry
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.NumberInput
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.RestOtpApiService
import pe.com.scotiabank.blpm.android.data.repository.otp.OtpRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class OtpVerificationCoordinatorFactory(
    private val hub: Hub,
    private val factoryOfChannelRegistry: FactoryOfChannelRegistry,
    private val numberInput: NumberInput,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    private val retrofit: Retrofit
        get() = hub.appModel.sessionRetrofit

    fun create(
        operation: CharArray,
        eventOnOtpVerified: Any,
    ): OtpVerificationCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        val channelRegistry: ChannelRegistry = factoryOfChannelRegistry.createFrom(hub.appModel)
        val factoryOfMainTopComposite = createFactoryOfMainTopComposite(
            uiStateHolder = uiStateHolder,
            channelRegistry = channelRegistry,
            idRegistry = idRegistry,
        )
        val repository: OtpRepository = createRepository()

        return OtpVerificationCoordinator(
            titleText = hub.weakResources.get()?.getString(R.string.title_activity_digital_key).orEmpty(),
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = factoryOfMainTopComposite,
            factoryOfMainBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            weakResources = hub.weakResources,
            channelRegistry = channelRegistry,
            numberInput = numberInput,
            idRegistry = idRegistry,
            availabilityRegistry = createAvailabilityRegistry(idRegistry),
            newOtpRequestModel = NewOtpRequestModel(hub.dispatcherProvider, repository, operation),
            otpVerificationModel = createOtpVerificationModel(repository, eventOnOtpVerified),
            errorTextOnFieldRequired = hub.errorTextOnFieldRequired,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
    )

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

    private fun createAvailabilityRegistry(idRegistry: IdRegistry): AvailabilityRegistry {
        val ids: Collection<Long> = listOf(
            idRegistry.idOfContinueButton,
        )
        return AvailabilityRegistry(ids)
    }

    private fun createRepository(): OtpRepository {
        val api: RestOtpApiService = retrofit.create(RestOtpApiService::class.java)
        return OtpRepository(api, hub.objectMapper)
    }

    private fun createOtpVerificationModel(
        repository: OtpRepository,
        eventOnOtpVerified: Any,
    ): OtpVerificationModel = OtpVerificationModel(
        dispatcherProvider = hub.dispatcherProvider,
        repository = repository,
        eventOnOtpVerified = eventOnOtpVerified,
    )
}
