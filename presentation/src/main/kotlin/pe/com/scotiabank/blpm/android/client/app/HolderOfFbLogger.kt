package pe.com.scotiabank.blpm.android.client.app

import android.app.Application
import com.facebook.appevents.AppEventsLogger
import pe.com.scotiabank.blpm.android.client.base.facebook.FactoryOfFbEventLogger.create
import java.lang.ref.WeakReference

interface HolderOfFbLogger {

    val fbEventLogger: AppEventsLogger?

}

class DelegateOfFbLogger(private val weakApp: WeakReference<out Application?>) : HolderOfFbLogger {

    override val fbEventLogger: AppEventsLogger? = createEventLogger()

    private fun createEventLogger(): AppEventsLogger? {
        val weakApp: Application = weakApp.get() ?: return null
        return create(weakApp)
    }

}
