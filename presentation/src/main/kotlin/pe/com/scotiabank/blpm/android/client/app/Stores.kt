package pe.com.scotiabank.blpm.android.client.app

import pe.com.scotiabank.blpm.android.client.assistance.model.ContextualAssistance
import pe.com.scotiabank.blpm.android.client.assistance.model.StatusBadge

interface StoreOfAppPackageInfo {

    fun isFirstInstall(): Boolean
}

interface StoreOfActivityLifecycle {

    val isAppCreatedByMainLauncher: Boolean
}

interface StoreOfAssistanceUi {

    val contextualAssistance: ContextualAssistance
    val statusBadge: StatusBadge
}
