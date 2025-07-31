package pe.com.scotiabank.blpm.android.client.base.session

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.session.entities.CommercialNotificationStatus
import pe.com.scotiabank.blpm.android.client.loop2pay.seed.Loop2PaySeed
import pe.com.scotiabank.blpm.android.client.base.session.entities.PersonType
import pe.com.scotiabank.blpm.android.client.base.session.entities.Profile
import pe.com.scotiabank.blpm.android.client.base.session.entities.contactpay.ScotiaPayStatus
import pe.com.scotiabank.blpm.android.client.host.session.SessionOpening
import pe.com.scotiabank.blpm.android.client.model.BenefitModel
import pe.com.scotiabank.blpm.android.client.model.OtherBankSeedModel
import pe.com.scotiabank.blpm.android.client.model.loop2pay.ContactModel
import pe.com.scotiabank.blpm.android.client.newdashboard.*
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.qrpayment.seed.QRSeed
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew
import retrofit2.Retrofit
import java.net.URI

class SessionModel(
    dispatcherProvider: DispatcherProvider,
    private val environmentHolder: EnvironmentHolder,
    private val modelFactoryForBusinessDashboard: BusinessDashboardModel.Factory,
    private val modelFactoryForPersonalDashboard: PersonalDashboardModel.Factory,
): SessionHost, DispatcherProvider by dispatcherProvider {

    private var sessionDelegate: SessionDelegate = BlankSessionDelegate(environmentHolder)

    override val isOpenedSession: Boolean
        get() = sessionDelegate.isOpenedSession

    override val sessionRetrofit: Retrofit
        get() = sessionDelegate.sessionRetrofit

    override fun saveChangesIf(
        nickName: CharArray,
        avatar: CharArray,
        isQrDeepLinkAvailable: Boolean,
        isContactPayQrAvailable: Boolean,
    ) {
        // Not required since it's already handled elsewhere
    }

    override suspend fun logout() {
        // Not required since it's already handled elsewhere
    }

    override fun supplyCookieStrings(uri: URI): List<String> {
        return sessionDelegate.supplyCookieStrings(uri)
    }

    override fun removeCookiesFromMemory() {
        sessionDelegate.removeCookiesFromMemory()
    }

    override fun removeUserIf() {
        sessionDelegate.removeUserIf()
    }

    override val profile: Profile
        get() = sessionDelegate.profile

    override val dashboardType: DashboardType
        get() = sessionDelegate.dashboardType

    override val navigationTemplate: NavigationTemplate
        get() = sessionDelegate.navigationTemplate

    override var commercialNotificationStatus: CommercialNotificationStatus
        get() = sessionDelegate.commercialNotificationStatus
        set(value) {
            sessionDelegate.commercialNotificationStatus = value
        }

    override var whatsNew: WhatsNew
        get() = sessionDelegate.whatsNew
        set(value) {
            sessionDelegate.whatsNew = value
        }

    override val otherSeed: Flow<OtherBankSeedModel>
        get() = sessionDelegate.otherSeed

    override var scotiaPayStatus: ScotiaPayStatus
        get() = sessionDelegate.scotiaPayStatus
        set(value) {
            sessionDelegate.scotiaPayStatus = value
        }

    override val isContactPayQrAvailable: Boolean
        get() = sessionDelegate.isContactPayQrAvailable

    override val benefits: Flow<BenefitModel>
        get() = sessionDelegate.benefits

    override var loop2PaySeed: Loop2PaySeed
        get() = sessionDelegate.loop2PaySeed
        set(value) {
            sessionDelegate.loop2PaySeed = value
        }
    override var qrSeed: QRSeed
        get() = sessionDelegate.qrSeed
        set(value) {
            sessionDelegate.qrSeed = value
        }

    override var loop2PayContacts: List<ContactModel>
        get() = sessionDelegate.loop2PayContacts
        set(value) {
            sessionDelegate.loop2PayContacts = value
        }

    override var isAnyLoanFound: Boolean
        get() = sessionDelegate.isAnyLoanFound
        set(value) {
            sessionDelegate.isAnyLoanFound = value
        }

    override var isRefreshNeeded: Boolean
        get() = sessionDelegate.isRefreshNeeded
        set(value) {
            sessionDelegate.isRefreshNeeded = value
        }

    override val products: String
        get() = sessionDelegate.products

    private val platformType: String
        get() = sessionDelegate.dashboardType.analyticsPlatformType

    override val platformTypeSupplying: Supplier<String> = Supplier(::platformType)

    private val personType: String
        get() = sessionDelegate.profile.client?.personType?.id?.lowercase() ?: Constant.HYPHEN_STRING

    override var token: String?
        get() = sessionDelegate.token
        set(value) {
            sessionDelegate.token = value
        }

    override val personTypeSupplying: Supplier<String> = Supplier(::personType)

    private val handlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            SessionOpening::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleOpeningOfSession)
        )
        .add(
            DashboardEvent::class,
            InstancePredicate(FilterOfDashboardEvent::filterInSwapping),
            SuspendingHandlerOfInstance(::handleSwapping)
        )
        .add(
            SessionEvent::class,
            InstancePredicate(FilterOfSessionEvent::filterInEnding),
            SuspendingHandlerOfInstance(::handleEndingOfSession)
        )
        .build()
    private val selfReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = handlingStore,
    )

    override suspend fun <A : Any> receive(instance: A): Boolean = withContext(ioDispatcher) {
        selfReceiver.receive(instance)
    }

    private fun handleOpeningOfSession(sessionOpening: SessionOpening) {
        val personType: PersonType = sessionOpening.profile.client?.personType ?: return

        sessionDelegate = createSessionDelegateByPersonType(personType, sessionOpening)
    }

    private fun createSessionDelegateByPersonType(
        personType: PersonType,
        sessionOpening: SessionOpening,
    ): SessionDelegate = when (personType) {
        PersonType.JURIDICAL_PERSON -> SessionDelegateForBusinessBankingOnly(
            sessionOpening = sessionOpening,
            modelFactoryForBusinessDashboard = modelFactoryForBusinessDashboard,
        )
        PersonType.NATURAL_BUSINESS -> SessionDelegateForPersonalBusinessBanking(
            sessionOpening = sessionOpening,
            modelFactoryForPersonalDashboard = modelFactoryForPersonalDashboard,
            modelFactoryForBusinessDashboard = modelFactoryForBusinessDashboard,
        )
        else -> SessionDelegateForPersonalBankingOnly(
            sessionOpening = sessionOpening,
            modelFactoryForPersonalDashboard = modelFactoryForPersonalDashboard,
        )
    }

    override fun setProducts(newProducts: List<NewProductModel>) {
        sessionDelegate.setProducts(newProducts)
    }

    override fun addChild(child: PortableViewModel) {
        sessionDelegate.addChild(child)
    }

    override fun removeChild(childId: Long) {
        sessionDelegate.removeChild(childId)
    }

    override fun receiveEvent(event: Any): Boolean = sessionDelegate.receiveEvent(event)

    private fun handleSwapping(event: DashboardEvent) {
        sessionDelegate.receive(event)
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleEndingOfSession(event: SessionEvent) {
        sessionDelegate.logout()
        sessionDelegate = BlankSessionDelegate(environmentHolder)
    }
}
