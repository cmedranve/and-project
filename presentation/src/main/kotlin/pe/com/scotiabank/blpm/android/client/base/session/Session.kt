package pe.com.scotiabank.blpm.android.client.base.session

import pe.com.scotiabank.blpm.android.client.host.user.UserModel

interface Session:
    HolderOfSessionRetrofit,
    UserModel,
    HolderOfProfile,
    HolderOfDashboardType,
    HolderOfNavigationTemplate,
    HolderOfCommercialNotification,
    HolderOfWhatsNew,
    HolderOfOtherSeed,
    HolderOfLoop2PaySeed,
    HolderOfLoop2PayContacts,
    HolderOfQRSeed,
    HolderOfProductList,
    HolderOfBenefits,
    HolderOfContactPay,
    HolderOfTokenFirebase,
    EventChannel
{

    val isOpenedSession: Boolean
}
