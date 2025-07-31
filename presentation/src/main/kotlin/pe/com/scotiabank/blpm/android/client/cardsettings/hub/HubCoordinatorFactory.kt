package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.CardSettingsFactory
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.success.SuccessFactory
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.cardsettings.analytics.list.CardSettingsAnalyticModel
import pe.com.scotiabank.blpm.android.client.cardsettings.analytics.success.SuccessAnalyticModel
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.screen.findTemplateForNewCardSettingsEntry
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.RestCardSettingApiService
import pe.com.scotiabank.blpm.android.data.net.RestCardSettingsDataApiService
import pe.com.scotiabank.blpm.android.data.repository.OldCardSettingsDataRepository
import pe.com.scotiabank.blpm.android.data.repository.cardsettings.CardSettingDataRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class HubCoordinatorFactory(
    private val hub: Hub,
    private val retrofit: Retrofit,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    private val appModel: AppModel
        get() = hub.appModel

    fun create(): HubCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()

        val idRegistry = IdRegistry()

        val ownerTypes: List<AtmCardOwnerType> = listOf(
            AtmCardOwnerType.MAIN_HOLDER,
            AtmCardOwnerType.JOINT_HOLDER,
            AtmCardOwnerType.JOINT_HOLDER_TO_OTHER,
        )

        val pushOtpFlowChecker = PushOtpFlowChecker(hub.appModel)

        val templateForNewCardSettings: OptionTemplate = findTemplateForNewCardSettingsEntry(
            navigation = appModel.navigationTemplate,
        )
        val isNewDetailScreen: Boolean = templateForNewCardSettings.isVisible && pushOtpFlowChecker.isPushOtpEnabled

        return HubCoordinator(
            factoryOfAppBarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(uiStateHolder, ownerTypes),
            factoryOfErrorBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            isNewDetailScreen = isNewDetailScreen,
            weakResources = hub.weakResources,
            appModel = appModel,
            groupModel = createGroupModel(isNewDetailScreen),
            detailModel = createDetailModel(isNewDetailScreen),
            textProvider = TextProvider(hub.weakResources),
            idRegistry = idRegistry,
            visitRegistry = createVisitRegistry(idRegistry),
            availabilityRegistry = createAvailabilityRegistry(idRegistry),
            analyticConsumer = createCardSettingsAnalyticModel(),
            analyticConsumerForSuccess = createSuccessAnalyticModel(),
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
        ownerTypes: List<AtmCardOwnerType>,
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        uiStateHolder = uiStateHolder,
        appModel = appModel,
        weakResources = hub.weakResources,
        weakAppContext = hub.weakAppContext,
        factory = hub.factoryOfOneColumnTextEntity,
        ownerTypes = ownerTypes,
    )

    private fun createVisitRegistry(idRegistry: IdRegistry): VisitRegistry {
        val maxNumberAllowedById: Map<Long, Int> = mapOf(
            idRegistry.retryButtonId to Int.MAX_VALUE,
        )
        return VisitRegistry(maxNumberAllowedById)
    }

    private fun createAvailabilityRegistry(idRegistry: IdRegistry): AvailabilityRegistry {
        val ids: Collection<Long> = mutableListOf(
            idRegistry.retryButtonId,
            idRegistry.cardGroupId,
        )
        return AvailabilityRegistry(ids)
    }

    private fun createGroupModel(
        isNewDetailScreen: Boolean,
    ): SuspendingFunction<Map<Long, Any?>, CardSettingHub> {

        if (isNewDetailScreen) {
            val newModel: NewCardSettingHubModel = createNewGroupModel()
            return SuspendingFunction(newModel::getCardSettingHub)
        }

        val oldModel: CardSettingHubModel = createOldGroupModel()
        return SuspendingFunction(oldModel::getCardSettingHub)
    }

    private fun createNewGroupModel(): NewCardSettingHubModel {
        val api: RestCardSettingApiService = retrofit.create(RestCardSettingApiService::class.java)
        val repository = CardSettingDataRepository(api, hub.objectMapper)
        val mapper = NewCardSettingHubMapper()
        return NewCardSettingHubModel(
            dispatcherProvider = hub.dispatcherProvider,
            repository = repository,
            hubMapper = mapper,
        )
    }

    private fun createOldGroupModel(): CardSettingHubModel {
        val api: RestCardSettingsDataApiService = retrofit.create(RestCardSettingsDataApiService::class.java)
        val repository = OldCardSettingsDataRepository(api, hub.objectMapper)
        val mapper = CardSettingHubMapper()
        return CardSettingHubModel(
            dispatcherProvider = hub.dispatcherProvider,
            repository = repository,
            hubMapper = mapper,
        )
    }

    private fun createDetailModel(isNewDetailScreen: Boolean): SuspendingFunction<String, Any> {

        if (isNewDetailScreen) return EmptyModel()

        val api: RestCardSettingsDataApiService = retrofit.create(RestCardSettingsDataApiService::class.java)
        val repository = OldCardSettingsDataRepository(api, hub.objectMapper)
        return CardSettingDetailModel(
            dispatcherProvider = hub.dispatcherProvider,
            weakResources = hub.weakResources,
            repository = repository,
        )
    }

    private fun createCardSettingsAnalyticModel(): CardSettingsAnalyticModel {
        val analyticFactory = CardSettingsFactory(hub.systemDataFactory)
        return CardSettingsAnalyticModel(hub.analyticsDataGateway, analyticFactory)
    }

    private fun createSuccessAnalyticModel(): SuccessAnalyticModel {
        val analyticFactory = SuccessFactory(hub.systemDataFactory)
        return SuccessAnalyticModel(hub.analyticsDataGateway, analyticFactory)
    }
}
