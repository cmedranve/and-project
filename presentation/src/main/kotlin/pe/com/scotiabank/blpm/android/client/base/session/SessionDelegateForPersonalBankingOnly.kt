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
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.newdashboard.PersonalDashboardModel
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.qrpayment.seed.QRSeed
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew

class SessionDelegateForPersonalBankingOnly(
    sessionOpening: SessionOpening,
    modelFactoryForPersonalDashboard: PersonalDashboardModel.Factory,
    seedHolder: SeedHolderForPeerToPeer = SeedHolderForPeerToPeer(),
): SessionDelegate,
    HolderOfSessionRetrofit by sessionOpening,
    UserModel by sessionOpening,
    HolderOfProfile by sessionOpening
{

    override val isOpenedSession: Boolean = true

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val dashboardModel: DashboardModel = modelFactoryForPersonalDashboard.create(
        sessionOpening = sessionOpening,
        receiverOfDashboardEvents = selfReceiver,
    )

    override val dashboardType: DashboardType
        get() = dashboardModel.dashboardType

    override val navigationTemplate: NavigationTemplate
        get() = dashboardModel.navigationTemplate

    override var commercialNotificationStatus: CommercialNotificationStatus
        get() = dashboardModel.commercialNotificationStatus
        set(value) {
            dashboardModel.commercialNotificationStatus = value
        }

    override var whatsNew: WhatsNew
        get() = dashboardModel.whatsNew
        set(value) {
            dashboardModel.whatsNew = value
        }

    override val otherSeed: Flow<OtherBankSeedModel>
        get() = dashboardModel.otherSeed

    override var scotiaPayStatus: ScotiaPayStatus
        get() = dashboardModel.scotiaPayStatus
        set(value) {
            dashboardModel.scotiaPayStatus = value
        }

    override val isContactPayQrAvailable: Boolean
        get() = dashboardModel.isContactPayQrAvailable

    override val benefits: Flow<BenefitModel>
        get() = dashboardModel.benefits

    override var loop2PaySeed: Loop2PaySeed = seedHolder.loop2PaySeed
    override var qrSeed: QRSeed = seedHolder.qrSeed

    override var loop2PayContacts: List<ContactModel>
        get() = dashboardModel.loop2PayContacts
        set(value) {
            dashboardModel.loop2PayContacts = value
        }

    override var isAnyLoanFound: Boolean
        get() = dashboardModel.isAnyLoanFound
        set(value) {
            dashboardModel.isAnyLoanFound = value
        }

    override var isRefreshNeeded: Boolean
        get() = dashboardModel.isRefreshNeeded
        set(value) {
            dashboardModel.isRefreshNeeded = value
        }

    override var token: String? = null

    override val products: String
        get() = dashboardModel.products

    override fun setProducts(newProducts: List<NewProductModel>) {
        dashboardModel.setProducts(newProducts)
    }

    override fun <A : Any> receive(instance: A): Boolean = selfReceiver.receive(instance)

    override fun addChild(child: PortableViewModel) {
        dashboardModel.addChild(child)
    }

    override fun removeChild(childId: Long) {
        dashboardModel.removeChild(childId)
    }

    override fun receiveEvent(event: Any): Boolean = dashboardModel.receiveEvent(event)
}
