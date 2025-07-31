package pe.com.scotiabank.blpm.android.client.base.session

import kotlinx.coroutines.flow.Flow
import pe.com.scotiabank.blpm.android.client.base.session.entities.CommercialNotificationStatus
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
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew
import retrofit2.Retrofit

interface HolderOfSessionRetrofit {

    val sessionRetrofit: Retrofit
}

interface HolderOfProfile {

    val profile: Profile
}

interface HolderOfDashboardType {

    val dashboardType: DashboardType
}

interface HolderOfNavigationTemplate {

    val navigationTemplate: NavigationTemplate
}

interface HolderOfCommercialNotification {

    var commercialNotificationStatus: CommercialNotificationStatus
}

interface HolderOfWhatsNew {

    var whatsNew: WhatsNew
}

interface HolderOfOtherSeed {

    val otherSeed: Flow<OtherBankSeedModel>
}

interface HolderOfLoop2PaySeed {

    var loop2PaySeed: Loop2PaySeed
}

interface HolderOfLoop2PayContacts {

    var loop2PayContacts: List<ContactModel>
}

interface HolderOfQRSeed {

    var qrSeed: QRSeed
}

interface HolderOfProductList {

    var isAnyLoanFound: Boolean
    var isRefreshNeeded: Boolean

    val products: String

    fun setProducts(newProducts: List<NewProductModel>)
}

interface HolderOfBenefits {

    val benefits: Flow<BenefitModel>
}

interface HolderOfContactPay {

    var scotiaPayStatus: ScotiaPayStatus
    val isContactPayQrAvailable: Boolean
}

interface HolderOfTokenFirebase {

    var token: String?
}
