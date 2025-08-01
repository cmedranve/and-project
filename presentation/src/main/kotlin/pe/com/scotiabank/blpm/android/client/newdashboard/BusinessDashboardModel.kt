package pe.com.scotiabank.blpm.android.client.newdashboard

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import pe.com.scotiabank.blpm.android.analytics.AnalyticsUserGateway
import pe.com.scotiabank.blpm.android.client.base.session.entities.CommercialNotificationStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.PersonType
import pe.com.scotiabank.blpm.android.client.base.session.entities.Profile
import pe.com.scotiabank.blpm.android.client.base.session.entities.contactpay.ScotiaPayStatus
import pe.com.scotiabank.blpm.android.client.host.session.SessionOpening
import pe.com.scotiabank.blpm.android.client.model.BenefitModel
import pe.com.scotiabank.blpm.android.client.model.OtherBankSeedModel
import pe.com.scotiabank.blpm.android.client.model.loop2pay.ContactModel
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.profilesettings.digitalkey.OtherBankSeedMapper
import pe.com.scotiabank.blpm.android.client.scotiapay.ScotiaPayMapper
import pe.com.scotiabank.blpm.android.client.scotiapay.shared.findTemplateForContactPayQr
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplateMapper
import pe.com.scotiabank.blpm.android.client.templates.TemplateFactory
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew
import pe.com.scotiabank.blpm.android.data.entity.scotiapay.VerificationResponseEntity
import pe.com.scotiabank.blpm.android.data.net.RestBusinessDashboardApiService
import pe.com.scotiabank.blpm.android.data.net.RestJoyBusinessApiService
import pe.com.scotiabank.blpm.android.data.repository.BusinessDashboardRepository
import pe.com.scotiabank.blpm.android.data.repository.TemplatesBusinessDataRepository
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import retrofit2.Retrofit
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class BusinessDashboardModel(
    private val profile: Profile,
    private val receiverOfDashboardEvents: InstanceReceiver,
    @Suppress("UNUSED_PROPERTY") private val paramGroup: ParamGroup,
    override var whatsNew: WhatsNew = WhatsNew(),
    override var loop2PayContacts: List<ContactModel> = emptyList()
): DashboardModel {

    override val dashboardType: DashboardType = DashboardType.BUSINESS

    override val navigationTemplate: NavigationTemplate = paramGroup.templateRepository
        .getTemplate()
        .onErrorReturnItem(TemplateFactory.createForBusinessProfile())
        .map(NavigationTemplateMapper::transform)
        .blockingSingle()

    override var commercialNotificationStatus: CommercialNotificationStatus = CommercialNotificationStatus.NONE

    override val otherSeed: Flow<OtherBankSeedModel> = paramGroup.businessRepository
        .otherSeed
        .map(OtherBankSeedMapper::transformOtherBankSeed)

    override val benefits: Flow<BenefitModel> = flow {
        emit(BenefitModel())
    }

    override var scotiaPayStatus: ScotiaPayStatus = paramGroup.businessRepository
        .getVerificationReturned()
        .onErrorReturnItem(VerificationResponseEntity(null))
        .map(ScotiaPayMapper()::toStatus)
        .blockingSingle()

    override val isContactPayQrAvailable: Boolean
        get() = scotiaPayStatus == ScotiaPayStatus.AFFILIATED
                && profile.client?.personType == PersonType.JURIDICAL_PERSON
                && findTemplateForContactPayQr(navigationTemplate).isVisible

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            DashboardEvent::class,
            InstancePredicate(FilterOfDashboardEvent::filterInSwapping),
            InstanceHandler(::handleSwapping)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val analyticsUserGateway: AnalyticsUserGateway = paramGroup.analyticsUserGateway

    override var isAnyLoanFound: Boolean = false
    override var isRefreshNeeded: Boolean = false

    private var _products: String = Constant.EMPTY_STRING
    override val products: String
        get() = _products

    private val weakChildrenById: MutableMap<Long, WeakReference<PortableViewModel>> = ConcurrentHashMap()

    init {
        sendUserProperties()
    }

    private fun sendUserProperties() {
        if (profile.hash.isNotBlank()) {
            analyticsUserGateway.sendUserHash(profile.hash, profile.client?.typeCreation)
        }
        analyticsUserGateway.sendPlatform(JOY_BUSINESS_DASHBOARD)
        profile.client?.personType?.id?.let(analyticsUserGateway::sendPersonType)
        profile.client?.segmentType?.let(analyticsUserGateway::sendCustomerSegment)
    }

    override fun <A : Any> receive(instance: A): Boolean = selfReceiver.receive(instance)

    @Suppress("UNUSED_PARAMETER")
    private fun handleSwapping(event: DashboardEvent) {
        receiverOfDashboardEvents.receive(DashboardEvent.PERSONAL_OPENING)
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
        val templateRepository: TemplatesBusinessDataRepository,
        val businessRepository: BusinessDashboardRepository,
        val analyticsUserGateway: AnalyticsUserGateway,
    )

    class Factory(
        private val analyticsUserGateway: AnalyticsUserGateway,
    ) {

        fun create(
            sessionOpening: SessionOpening,
            receiverOfDashboardEvents: InstanceReceiver,
        ): BusinessDashboardModel {

            val paramGroup: ParamGroup = createParamGroup(sessionOpening.sessionRetrofit)

            return BusinessDashboardModel(
                profile = sessionOpening.profile,
                receiverOfDashboardEvents = receiverOfDashboardEvents,
                paramGroup = paramGroup,
            )
        }

        private fun createParamGroup(retrofit: Retrofit): ParamGroup {
            val api: RestBusinessDashboardApiService = retrofit.create(
                RestBusinessDashboardApiService::class.java,
            )
            return ParamGroup(
                templateRepository = createTemplateRepository(retrofit),
                businessRepository = BusinessDashboardRepository(api),
                analyticsUserGateway = analyticsUserGateway,
            )
        }

        private fun createTemplateRepository(retrofit: Retrofit): TemplatesBusinessDataRepository {
            val api: RestJoyBusinessApiService = retrofit.create(
                RestJoyBusinessApiService::class.java,
            )
            return TemplatesBusinessDataRepository(api)
        }
    }

    companion object {

        private const val JOY_BUSINESS_DASHBOARD = "joy negocio"
    }
}