package pe.com.scotiabank.blpm.android.client.base.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.scotiabank.sdk.analytics.AnalyticClient
import com.scotiabank.sdk.analytics.AnalyticEvent
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
class AnalyticClientsModule {
    @Provides
    @IntoSet
    fun providesFirebaseClient(context: Context): AnalyticClient {
        return object : AnalyticClient {
            val analytics = FirebaseAnalytics.getInstance(context)

            override fun logEvent(event: AnalyticEvent) {
                val bundle = Bundle()
                event.forEach { bundle.putString(it.key, it.value) }
                val name = event.eventName
                if (name != null) {
                    bundle.remove(AnalyticEvent.EVENT_NAME)
                    analytics.logEvent(name, bundle)
                }
            }

            override fun getClientName(): String = "Firebase"

            override fun initClient(context: Context) = Unit
        }
    }
}
