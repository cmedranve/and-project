package pe.com.scotiabank.blpm.android.client.base.analytics

import android.content.Context
import com.scotiabank.sdk.analytics.AnalyticClient
import com.scotiabank.sdk.analytics.AnalyticConfig
import com.scotiabank.sdk.analytics.AnalyticManager
import dagger.Module
import dagger.Provides
import pe.com.scotiabank.blpm.android.client.base.ApplicationScope

@Module(includes = [AnalyticClientsModule::class])
class NewAnalyticsModule {
    @Provides
    @ApplicationScope
    fun providesAnalyticManager(
        clients: Set<@JvmSuppressWildcards AnalyticClient>,
        context: Context
    ): AnalyticManager {
        var config = AnalyticConfig.Builder().setContext(context)
        clients.forEach { config = config.registerClient(it) }
        val instance = AnalyticManager.getInstance()
        instance.setConfig(config.build())
        return instance
    }
}
