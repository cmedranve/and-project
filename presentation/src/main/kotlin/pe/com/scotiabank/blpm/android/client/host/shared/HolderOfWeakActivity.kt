package pe.com.scotiabank.blpm.android.client.host.shared

import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

interface HolderOfWeakActivity {

    val weakActivity: WeakReference<FragmentActivity?>
}