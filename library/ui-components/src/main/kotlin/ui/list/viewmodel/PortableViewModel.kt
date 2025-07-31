package pe.com.scotiabank.blpm.android.ui.list.viewmodel

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import java.lang.ref.WeakReference

interface PortableViewModel: LiveHolder, Recycling, EventHandler {

    val id: Long

    fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        // deprecated
    }

    fun setUpUi(weakActivity: WeakReference<FragmentActivity?>, intent: Intent) {
        // instead of the one-parameter function, use this as the former is deprecated
    }

    fun receiveNewIntent(intent: Intent) {
        // override if needed
    }
}
