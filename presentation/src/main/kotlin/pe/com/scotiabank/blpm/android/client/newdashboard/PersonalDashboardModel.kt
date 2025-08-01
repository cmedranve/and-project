package pe.com.scotiabank.blpm.android.client.newdashboard

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.scotiabank.enhancements.handling.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.com.scotiabank.blpm.android.analytics.AnalyticsUserGateway
import pe.com.scotiabank.blpm.android.client.base.session.entities.CommercialNotificationStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.Profile
import pe.com.scotiabank.blpm.android.client.base.session.entities.contactpay.ScotiaPayStatus
import pe.com.scotiabank.blpm.android.client.host.session.SessionOpening
import pe.com.scotiabank.blpm.android.client.messaging.NotificationSetting
import pe.com.scotiabank.blpm.android.client.model.BenefitModel
import pe.com.scotiabank.blpm.android.client.model.OtherBankSeedModel
import pe.com.scotiabank.blpm.android.client.model.loop2pay.ContactModel
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.profilesettings.ProfileSettingModelDataMapper
import pe.com.scotiabank.blpm.android.client.profilesettings.digitalkey.OtherBankSeedMapper
import pe.com.scotiabank.blpm.android.client.security.NotificationSharedPreferences
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplateMapper
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.templates.TemplateFactory
import pe.com.scotiabank.blpm.android.client.util.*
import pe.com.scotiabank.blpm.android.client.util.Constant.EMPTY_STRING
import pe.com.scotiabank.blpm.android.client.whatsnew.WhatsNewMapper
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew
import pe.com.scotiabank.blpm.android.data.entity.messaging.NotificationVinculationRequest
import pe.com.scotiabank.blpm.android.data.entity.templates.NavigationTemplateEntity
import pe.com.scotiabank.blpm.android.data.mapper.messaging.NotificationMapper
import pe.com.scotiabank.blpm.android.data.model.messaging.NotificationValidationModel
import pe.com.scotiabank.blpm.android.data.model.messaging.NotificationValidationResult
import pe.com.scotiabank.blpm.android.data.model.messaging.NotificationVinculationModel
import pe.com.scotiabank.blpm.android.data.net.NotificationApiService
import pe.com.scotiabank.blpm.android.data.net.RestApiService
import pe.com.scotiabank.blpm.android.data.net.RestMessageApiService
import pe.com.scotiabank.blpm.android.data.net.RestPersonalDashboardApiService
import pe.com.scotiabank.blpm.android.data.repository.BenefitRepository
import pe.com.scotiabank.blpm.android.data.repository.PersonalDashboardRepository
import pe.com.scotiabank.blpm.android.data.repository.TemplatesDataRepository
import pe.com.scotiabank.blpm.android.data.repository.messaging.NotificationRepository
import pe.com.scotiabank.blpm.android.data.repository.releaseoverview.WhatsNewRepository
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import retrofit2.Retrofit
import java.lang.ref.WeakReference
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PersonalDashboardModel(
    private val profile: Profile,
    private val receiverOfDashboardEvents: InstanceReceiver,
    @Suppress("UNUSED_PROPERTY") private val paramGroup: ParamGroup,
): DashboardModel {

    override val dashboardType: DashboardType = DashboardType.PERSONAL

    private val defaultTemplateEntity: NavigationTemplateEntity
        get() = when {
            profile.isFP() -> TemplateFactory.createForFullProfile()
            profile.isRP() -> TemplateFactory.createForRestrictedProfile()
            profile.isOM() -> TemplateFactory.createForOpenMarket()
            else -> TemplateFactory.createForFullProfile()
        }

    override val navigationTemplate: NavigationTemplate = paramGroup.templateRepository
        .getTemplate()
        .onErrorReturnItem(defaultTemplateEntity)
        .map(NavigationTemplateMapper::transform)
        .blockingSingle()

    override var commercialNotificationStatus: CommercialNotificationStatus = CommercialNotificationStatus.NONE

    override var whatsNew: WhatsNew = paramGroup.whatsNewRepository
        .getWhatsNew()
        .map(WhatsNewMapper::toWhatsNew)
        .onErrorReturnItem(WhatsNew())
        .blockingSingle()

    override val otherSeed: Flow<OtherBankSeedModel> = paramGroup.personalRepository
        .otherSeed
        .map(OtherBankSeedMapper::transformOtherBankSeed)

    override var scotiaPayStatus: ScotiaPayStatus = ScotiaPayStatus.UNSUITABLE

    override val isContactPayQrAvailable: Boolean = false

    override val benefits: Flow<BenefitModel> = paramGroup.benefitRepository
        .benefit
        .map(ProfileSettingModelDataMapper::transformBenefit)

    override var loop2PayContacts: List<ContactModel> = emptyList()

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            DashboardEvent::class,
            InstancePredicate(FilterOfDashboardEvent::filterInSwapping),
            InstanceHandler(::handleSwapping)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val notificationRepository: NotificationRepository = paramGroup.notificationRepository
    private val notificationSharedPreferences: NotificationSharedPreferences = paramGroup.notificationSharedPreferences
    private val analyticsUserGateway: AnalyticsUserGateway = paramGroup.analyticsUserGateway

    override var isAnyLoanFound: Boolean = false
    override var isRefreshNeeded: Boolean = false

    private var _products: String = EMPTY_STRING
    override val products: String
        get() = _products

    private val weakChildrenById: MutableMap<Long, WeakReference<PortableViewModel>> = ConcurrentHashMap()

    init {
        sendUserProperties()
        synchronizeNotificationSettingWithRemote()
    }

    private fun sendUserProperties() {
        if (profile.hash.isNotBlank()) {
            analyticsUserGateway.sendUserHash(profile.hash, profile.client?.typeCreation)
        }
        analyticsUserGateway.sendPlatform(JOY_PERSONAL_DASHBOARD)
        profile.client?.personType?.id?.let(analyticsUserGateway::sendPersonType)
        profile.client?.segmentType?.let(analyticsUserGateway::sendCustomerSegment)
    }

    private fun synchronizeNotificationSettingWithRemote() {
        val isLastUserTheSameForPush: Boolean = NotificationSetting.lastUserIsSame(
            notificationSharedPreferences,
            profile.id
        )
        if (isLastUserTheSameForPush.not()) {
            NotificationSetting.setUUID(profile.id, notificationSharedPreferences)
        }

        if (profile.isFP().not()) return
        if (isNotificationLinkageVisible().not()) return

        setUpLocalNotificationSettingWith(isLastUserTheSameForPush)
    }

    private fun isNotificationLinkageVisible(): Boolean {
        val optionTemplate: OptionTemplate = TemplatesUtil.getOperation(
            navigationTemplate,
            NotificationSetting.NOTIFICATION_FEATURE,
            NotificationSetting.NOTIFICATION_VINCULATION_OPTION
        )
        return optionTemplate.isVisible
    }

    private fun setUpLocalNotificationSettingWith(isLastUserTheSameForPush: Boolean) {
        val result = getNotificationLinkageResult()
        if (result is NotificationValidationResult.Affiliate) {
            setUpAffiliatedUserForPush(result.model, isLastUserTheSameForPush)
            return
        }
        if (result is NotificationValidationResult.UnAffiliate) {
            setUpUnaffiliatedUserForPush(isLastUserTheSameForPush)
        }
    }

    private fun getNotificationLinkageResult(): NotificationValidationResult = notificationRepository
        .getValidation()
        .onErrorReturnItem(NotificationValidationResult.Nothing)
        .blockingSingle()

    private fun setUpAffiliatedUserForPush(
        validation: NotificationValidationModel,
        isLastUserTheSameForPush: Boolean
    ) {
        val linkageId: String = validation.vinculationId.orEmpty()
        val currentLinkageId: String = NotificationSetting
            .getVinculationId(notificationSharedPreferences)
            .orEmpty()

        val isAffiliated: Boolean = validation.isAffiliated ?: false
        NotificationSetting.setAffiliation(isAffiliated, notificationSharedPreferences)

        if (linkageId == currentLinkageId) {
            commercialNotificationStatus = CommercialNotificationStatus.LINKED_USER
            refreshFirebaseTokenIfNeeded()
            return
        }

        commercialNotificationStatus = CommercialNotificationStatus.OTHER_USER
        NotificationSetting.setUpOtherUser(notificationSharedPreferences, isLastUserTheSameForPush)
    }

    private fun refreshFirebaseTokenIfNeeded() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            updateNotificationToken(task)
        }
    }

    private fun updateNotificationToken(task: Task<String>) {
        if (!task.isSuccessful) return

        val lastToken: String = task.result
        val currentToken: String? = NotificationSetting.getLinkageToken(notificationSharedPreferences)
        if (currentToken == lastToken) return

        val linkageTimestamp: String? = NotificationSetting.getLinkageTimestamp(notificationSharedPreferences)
        val currentTimestamp: Long = Date().time
        if (linkageTimestamp.isNullOrEmpty()) {
            linkUser(lastToken)
            return
        }
        val millisecondsDiff = currentTimestamp - linkageTimestamp.toLong()
        val dayDiff: Long = TimeUnit.MILLISECONDS.toDays(millisecondsDiff)
        if (dayDiff >= Constant.MAX_DAYS_UPDATE_TOKEN) linkUser(lastToken)
    }

    private fun linkUser(lastToken: String) {
        val messagingRequest = NotificationVinculationRequest()
        messagingRequest.deviceToken = lastToken
        val timestamp = Date().time.toString()
        val vinculationId: String = getNewVinculationId(messagingRequest).vinculationId.orEmpty()
        NotificationSetting.storeVinculationId(vinculationId, notificationSharedPreferences)
        NotificationSetting.setLinkageToken(lastToken, notificationSharedPreferences)
        NotificationSetting.setLinkageTimestamp(timestamp, notificationSharedPreferences)
    }

    private fun getNewVinculationId(
        messagingRequest: NotificationVinculationRequest
    ): NotificationVinculationModel = notificationRepository.setVinculation(messagingRequest)
        .onErrorReturnItem(NotificationVinculationModel(EMPTY_STRING))
        .blockingSingle()

    private fun setUpUnaffiliatedUserForPush(isLastUserTheSameForPush: Boolean) {
        commercialNotificationStatus = CommercialNotificationStatus.NEW_USER
        NotificationSetting.setUpNewUser(notificationSharedPreferences, isLastUserTheSameForPush)
    }

    override fun <A : Any> receive(instance: A): Boolean = selfReceiver.receive(instance)

    @Suppress("UNUSED_PARAMETER")
    private fun handleSwapping(event: DashboardEvent) {
        receiverOfDashboardEvents.receive(DashboardEvent.BUSINESS_OPENING)
    }

    override fun setProducts(newProducts: List<NewProductModel>) {
        _products = newProducts.joinToString(separator = Constant.COMMA, transform = ::toName)
    }

    private fun toName(newProduct: NewProductModel): String = newProduct.name

    override fun addChild(child: PortableViewModel) {
        weakChildrenById[child.id] = WeakReference(child)
    }

    override fun removeChild(childId: Long) {
        weakChildrenById.remove(childId)
    }

    override fun receiveEvent(event: Any): Boolean {
        val handlingResults: List<Boolean> = weakChildrenById
            .map { weakChildById -> notifyEvent(event, weakChildById.value) }
        return handlingResults.any { handlingResult -> handlingResult }
    }

    private fun notifyEvent(
        event: Any,
        weakChild: WeakReference<PortableViewModel>,
    ): Boolean = weakChild.get()?.receiveEvent(event) ?: false

    class ParamGroup(
        val templateRepository: TemplatesDataRepository,
        val whatsNewRepository: WhatsNewRepository,
        val notificationRepository: NotificationRepository,
        val notificationSharedPreferences: NotificationSharedPreferences,
        val personalRepository: PersonalDashboardRepository,
        val benefitRepository: BenefitRepository,
        val analyticsUserGateway: AnalyticsUserGateway,
    )

    class Factory(
        private val notificationSharedPreferences: NotificationSharedPreferences,
        private val analyticsUserGateway: AnalyticsUserGateway,
    ) {


        fun create(
            sessionOpening: SessionOpening,
            receiverOfDashboardEvents: InstanceReceiver,
        ): PersonalDashboardModel {

            val paramGroup: ParamGroup = createParamGroup(sessionOpening.sessionRetrofit)

            return PersonalDashboardModel(
                profile = sessionOpening.profile,
                receiverOfDashboardEvents = receiverOfDashboardEvents,
                paramGroup = paramGroup,
            )
        }

        private fun createParamGroup(retrofit: Retrofit): ParamGroup {
            val api: RestPersonalDashboardApiService = retrofit.create(
                RestPersonalDashboardApiService::class.java,
            )
            return ParamGroup(
                templateRepository = createTemplateRepository(retrofit),
                whatsNewRepository = createWhatsNewRepository(retrofit),
                notificationRepository = createNotificationRepository(retrofit),
                notificationSharedPreferences = notificationSharedPreferences,
                personalRepository = PersonalDashboardRepository(api),
                benefitRepository = BenefitRepository(api),
                analyticsUserGateway = analyticsUserGateway,
            )
        }

        private fun createTemplateRepository(retrofit: Retrofit): TemplatesDataRepository {
            val api: RestApiService = retrofit.create(RestApiService::class.java)
            return TemplatesDataRepository(api)
        }

        private fun createWhatsNewRepository(retrofit: Retrofit): WhatsNewRepository {
            val api: RestMessageApiService = retrofit.create(RestMessageApiService::class.java)
            return WhatsNewRepository(api)
        }

        private fun createNotificationRepository(retrofit: Retrofit): NotificationRepository {
            val api: NotificationApiService = retrofit.create(NotificationApiService::class.java)
            val mapper = NotificationMapper()
            return NotificationRepository(api, mapper)
        }
    }

    companion object {

        private const val JOY_PERSONAL_DASHBOARD = "joy persona"
    }
}
