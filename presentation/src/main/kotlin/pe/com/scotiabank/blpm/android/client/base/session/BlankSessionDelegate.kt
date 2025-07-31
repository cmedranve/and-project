package pe.com.scotiabank.blpm.android.client.base.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.network.ExternalEnvironment
import pe.com.scotiabank.blpm.android.client.base.session.entities.CommercialNotificationStatus
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import pe.com.scotiabank.blpm.android.client.loop2pay.seed.Loop2PaySeed
import pe.com.scotiabank.blpm.android.client.base.session.entities.Profile
import pe.com.scotiabank.blpm.android.client.base.session.entities.contactpay.ScotiaPayStatus
import pe.com.scotiabank.blpm.android.client.model.BenefitModel
import pe.com.scotiabank.blpm.android.client.model.OtherBankSeedModel
import pe.com.scotiabank.blpm.android.client.model.loop2pay.ContactModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.qrpayment.seed.QRSeed
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew
import retrofit2.Retrofit
import java.lang.ref.WeakReference
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

class BlankSessionDelegate(private val environmentHolder: EnvironmentHolder): SessionDelegate {

    override val isOpenedSession: Boolean = false

    private val environment: ExternalEnvironment
        get() = environmentHolder.environment

    override val sessionRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(environment.baseUrlOfApi)
            .build()
    }

    override fun saveChangesIf(
        nickName: CharArray,
        avatar: CharArray,
        isQrDeepLinkAvailable: Boolean,
        isContactPayQrAvailable: Boolean,
    ) {
        // Not required
    }

    override suspend fun logout() {
        // Not required
    }

    override fun supplyCookieStrings(uri: URI): List<String> = emptyList()

    override fun removeCookiesFromMemory() {
        // Not required
    }

    override fun removeUserIf() {
        // Not required
    }

    override val profile: Profile by lazy {
        Profile()
    }

    override val dashboardType: DashboardType = DashboardType.PERSONAL

    override val navigationTemplate: NavigationTemplate by lazy {
        NavigationTemplate()
    }

    override var commercialNotificationStatus: CommercialNotificationStatus = CommercialNotificationStatus.NONE

    override var whatsNew: WhatsNew = WhatsNew()
    override val otherSeed: Flow<OtherBankSeedModel> = flow {
        emit(OtherBankSeedModel())
    }

    override var scotiaPayStatus: ScotiaPayStatus = ScotiaPayStatus.UNSUITABLE

    override val isContactPayQrAvailable: Boolean = false

    override val benefits: Flow<BenefitModel> =  flow {
        emit(BenefitModel())
    }

    override var loop2PaySeed: Loop2PaySeed = Loop2PaySeed()
    override var qrSeed: QRSeed = QRSeed()

    override var loop2PayContacts: List<ContactModel> = emptyList()

    override var isAnyLoanFound: Boolean = false
    override var isRefreshNeeded: Boolean = false

    override val products: String = Constant.EMPTY_STRING
    override var token: String? = null

    private val weakChildrenById: MutableMap<Long, WeakReference<PortableViewModel>> = ConcurrentHashMap()

    override fun setProducts(newProducts: List<NewProductModel>) {
        // Not required
    }

    override fun <A : Any> receive(instance: A): Boolean = true

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
}
