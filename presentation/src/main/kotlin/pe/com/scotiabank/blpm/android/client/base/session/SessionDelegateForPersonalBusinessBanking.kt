package pe.com.scotiabank.blpm.android.client.base.session

import com.scotiabank.enhancements.handling.*
import kotlinx.coroutines.flow.Flow
import pe.com.scotiabank.blpm.android.client.base.session.entities.CommercialNotificationStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.contactpay.ScotiaPayStatus
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import pe.com.scotiabank.blpm.android.client.loop2pay.seed.Loop2PaySeed
import pe.com.scotiabank.blpm.android.client.host.session.SessionOpening
import pe.com.scotiabank.blpm.android.client.host.user.UserModel
import pe.com.scotiabank.blpm.android.client.model.BenefitModel
import pe.com.scotiabank.blpm.android.client.model.OtherBankSeedModel
import pe.com.scotiabank.blpm.android.client.model.loop2pay.ContactModel
import pe.com.scotiabank.blpm.android.client.newdashboard.*
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.qrpayment.seed.QRSeed
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew

class SessionDelegateForPersonalBusinessBanking(
    sessionOpening: SessionOpening,
    private val modelFactoryForPersonalDashboard: PersonalDashboardModel.Factory,
    private val modelFactoryForBusinessDashboard: BusinessDashboardModel.Factory,
    seedHolder: SeedHolderForPeerToPeer = SeedHolderForPeerToPeer(),
): SessionDelegate,
    HolderOfSessionRetrofit by sessionOpening,
    UserModel by sessionOpening,
    HolderOfProfile by sessionOpening
{

    override val isOpenedSession: Boolean = true

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            DashboardEvent::class,
            InstancePredicate(FilterOfDashboardEvent::filterInSwapping),
            InstanceHandler(::handleSwapping)
        )
        .add(
            DashboardEvent::class,
            InstancePredicate(FilterOfDashboardEvent::filterInBusinessOpening),
            InstanceHandler(::handleOpeningOfBusinessDashboard)
        )
        .add(
            DashboardEvent::class,
            InstancePredicate(FilterOfDashboardEvent::filterInPersonalOpening),
            InstanceHandler(::handleOpeningOfPersonalDashboard)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val personalDashboardModel: DashboardModel by lazy {
        modelFactoryForPersonalDashboard.create(
            sessionOpening = sessionOpening,
            receiverOfDashboardEvents = selfReceiver,
        )
    }

    private val businessDashboardModel: DashboardModel by lazy {
        modelFactoryForBusinessDashboard.create(
            sessionOpening = sessionOpening,
            receiverOfDashboardEvents = selfReceiver,
        )
    }

    private val isPersonalProductListEmpty: Boolean
        get() = profile.products.isEmpty()

    private val isBusinessProductListEmpty: Boolean
        get() = profile.businessProducts.isEmpty()

    private val isBusinessFirst: Boolean
        get() = isPersonalProductListEmpty && isBusinessProductListEmpty.not()

    private var currentDashboardModel: DashboardModel = if (isBusinessFirst) {
        businessDashboardModel
    } else {
        personalDashboardModel
    }

    override val dashboardType: DashboardType
        get() = currentDashboardModel.dashboardType

    override val navigationTemplate: NavigationTemplate
        get() = currentDashboardModel.navigationTemplate

    override var commercialNotificationStatus: CommercialNotificationStatus
        get() = currentDashboardModel.commercialNotificationStatus
        set(value) {
            currentDashboardModel.commercialNotificationStatus = value
        }

    override var whatsNew: WhatsNew
        get() = currentDashboardModel.whatsNew
        set(value) {
            currentDashboardModel.whatsNew = value
        }

    override val otherSeed: Flow<OtherBankSeedModel>
        get() = currentDashboardModel.otherSeed

    override var scotiaPayStatus: ScotiaPayStatus
        get() = currentDashboardModel.scotiaPayStatus
        set(value) {
            currentDashboardModel.scotiaPayStatus = value
        }

    override val isContactPayQrAvailable: Boolean
        get() = currentDashboardModel.isContactPayQrAvailable

    override val benefits: Flow<BenefitModel>
        get() = currentDashboardModel.benefits

    override var loop2PaySeed: Loop2PaySeed = seedHolder.loop2PaySeed
    override var qrSeed: QRSeed = seedHolder.qrSeed

    override var loop2PayContacts: List<ContactModel>
        get() = currentDashboardModel.loop2PayContacts
        set(value) {
            currentDashboardModel.loop2PayContacts = value
        }

    override var isAnyLoanFound: Boolean
        get() = currentDashboardModel.isAnyLoanFound
        set(value) {
            currentDashboardModel.isAnyLoanFound = value
        }

    override var isRefreshNeeded: Boolean
        get() = currentDashboardModel.isRefreshNeeded
        set(value) {
            currentDashboardModel.isRefreshNeeded = value
        }

    override var token: String? = null

    override val products: String
        get() = currentDashboardModel.products

    override fun setProducts(newProducts: List<NewProductModel>) {
        currentDashboardModel.setProducts(newProducts)
    }

    override fun <A : Any> receive(instance: A): Boolean = selfReceiver.receive(instance)

    override fun addChild(child: PortableViewModel) {
        currentDashboardModel.addChild(child)
    }

    override fun removeChild(childId: Long) {
        currentDashboardModel.removeChild(childId)
    }

    override fun receiveEvent(event: Any): Boolean = currentDashboardModel.receiveEvent(event)

    private fun handleSwapping(event: DashboardEvent) {
        currentDashboardModel.receive(event)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOpeningOfBusinessDashboard(event: DashboardEvent) {
        currentDashboardModel = businessDashboardModel
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOpeningOfPersonalDashboard(event: DashboardEvent) {
        currentDashboardModel = personalDashboardModel
    }
}
