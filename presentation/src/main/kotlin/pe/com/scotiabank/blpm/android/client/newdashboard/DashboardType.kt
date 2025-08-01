package pe.com.scotiabank.blpm.android.client.newdashboard

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class DashboardType(
    val analyticsPlatformType: String,
    val labelForSwitching: String,
    val analyticLabelForSwitching: String,
) {

    BUSINESS(
        analyticsPlatformType = "joy negocio",
        labelForSwitching = Constant.PROFILE_BUSINESS_DESCRIPTION,
        analyticLabelForSwitching = "perfil-negocio",
    ),
    PERSONAL(
        analyticsPlatformType = "joy persona",
        labelForSwitching = Constant.PROFILE_PERSON_DESCRIPTION,
        analyticLabelForSwitching = "perfil-personal",
    ),
}