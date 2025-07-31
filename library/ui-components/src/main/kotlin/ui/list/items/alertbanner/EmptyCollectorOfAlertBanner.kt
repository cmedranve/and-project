package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class EmptyCollectorOfAlertBanner: CollectorOfAlertBanner {

    override fun collect(
        paddingEntity: UiEntityOfPadding,
        receiver: InstanceReceiver?,
        callback: Runnable?,
    ): List<UiEntityOfAlertBanner<Any>> = emptyList()
}
