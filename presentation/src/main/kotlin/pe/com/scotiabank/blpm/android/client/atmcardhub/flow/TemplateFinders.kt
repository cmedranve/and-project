package pe.com.scotiabank.blpm.android.client.atmcardhub.flow

import pe.com.scotiabank.blpm.android.client.templates.NavigationTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil

fun findTemplateForCardHub(navigation: NavigationTemplate) : OptionTemplate {
    return TemplatesUtil.getOperation(navigation, TemplatesUtil.DASHBOARD_KEY, TemplatesUtil.CARDS_HUB_KEY)
}
