package pe.com.scotiabank.blpm.android.client.newdashboard.mylist

import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil

fun findNavigationFeatureTemplate(navigationTemplate: NavigationTemplate): FeatureTemplate {
    return TemplatesUtil.getFeature(navigationTemplate, TemplatesUtil.NAVIGATION_KEY)
}

fun findMyListOptionTemplate(featureTemplate: FeatureTemplate): OptionTemplate {
    return TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.MY_LIST_KEY)
}

fun findMyListFeatureTemplate(navigationTemplate: NavigationTemplate): FeatureTemplate {
    return TemplatesUtil.getFeature(navigationTemplate, TemplatesUtil.MY_LIST_KEY)
}

fun findTemplateForTransfers(featureTemplate: FeatureTemplate): OptionTemplate {
    return TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.FREQUENT_TRANSFERS_KEY)
}

fun findTemplateForPayments(featureTemplate: FeatureTemplate): OptionTemplate {
    return TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.FREQUENT_PAYMENTS_KEY)
}

fun findTemplateForAddingRecentPayments(featureTemplate: FeatureTemplate): OptionTemplate {
    return TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.ADD_RECENT_PAYMENTS_KEY)
}