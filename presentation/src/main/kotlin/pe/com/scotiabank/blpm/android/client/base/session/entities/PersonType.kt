package pe.com.scotiabank.blpm.android.client.base.session.entities

import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.util.Constant


enum class PersonType(
    val id: String,
    val isQrDeepLinkAvailable: Boolean,
    val analyticValue: String,
    val platformType: String,
) {

    NATURAL_PERSON(
        id = Constant.NATURAL_PERSON_ID,
        isQrDeepLinkAvailable = true,
        analyticValue = AnalyticsConstant.NATURAL_PERSON,
        platformType = AnalyticsConstant.JOY_PERSON,
    ),
    NATURAL_BUSINESS(
        id = Constant.NATURAL_BUSINESS_ID,
        isQrDeepLinkAvailable = true,
        analyticValue = AnalyticsConstant.BUSINESS_PERSON,
        platformType = AnalyticsConstant.JOY_BUSINESS,
    ),
    JURIDICAL_PERSON(
        id = Constant.JURIDICAL_PERSON_ID,
        isQrDeepLinkAvailable = false,
        analyticValue = AnalyticsConstant.JURIDICAL_PERSON,
        platformType = AnalyticsConstant.JOY_BUSINESS,
    );

    companion object {

        @JvmStatic
        fun identifyBy(id: String): PersonType = when (id.uppercase()) {
            Constant.JURIDICAL_PERSON_ID -> JURIDICAL_PERSON
            Constant.NATURAL_BUSINESS_ID -> NATURAL_BUSINESS
            else -> NATURAL_PERSON
        }
    }
}
