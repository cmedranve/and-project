package pe.com.scotiabank.blpm.android.client.newdashboard

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.session.*

interface DashboardModel:
    HolderOfDashboardType,
    HolderOfNavigationTemplate,
    HolderOfCommercialNotification,
    HolderOfWhatsNew,
    HolderOfOtherSeed,
    HolderOfLoop2PayContacts,
    HolderOfProductList,
    HolderOfBenefits,
    HolderOfContactPay,
    InstanceReceiver,
    EventChannel
