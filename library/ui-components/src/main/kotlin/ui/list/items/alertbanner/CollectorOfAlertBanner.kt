package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

interface CollectorOfAlertBanner {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver? = null,
        callback: Runnable? = null,
    ): List<UiEntityOfAlertBanner<Any>>
}
