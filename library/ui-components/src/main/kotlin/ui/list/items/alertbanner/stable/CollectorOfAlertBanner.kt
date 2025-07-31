package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.stable

import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.UiEntityOfAlertBanner

interface CollectorOfAlertBanner {

    fun collect(): List<UiEntityOfAlertBanner<Any>>
}
