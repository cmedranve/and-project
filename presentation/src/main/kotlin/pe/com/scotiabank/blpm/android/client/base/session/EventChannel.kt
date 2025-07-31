package pe.com.scotiabank.blpm.android.client.base.session

import pe.com.scotiabank.blpm.android.ui.list.viewmodel.EventHandler
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel

interface EventChannel : EventHandler {

    fun addChild(child: PortableViewModel)

    fun removeChild(childId: Long)
}
