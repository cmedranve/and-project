package pe.com.scotiabank.blpm.android.client.base.module

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Dispatcher
import pe.com.scotiabank.blpm.android.client.base.ApplicationScope
import pe.com.scotiabank.blpm.android.client.base.network.DelegateFactoryOfOkHttpClient
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.network.FactoryDecoratorOfEnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.network.FactoryDelegateOfEnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.network.FactoryOfEnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.network.FactoryOfOkHttpClient

@Module
class ClientModule {

    @Provides
    @ApplicationScope
    fun provideOkHttpClientFactory(): FactoryOfOkHttpClient = DelegateFactoryOfOkHttpClient(
        dispatcher = Dispatcher(),
    )

    @Provides
    @ApplicationScope
    fun provideEnvironmentHolder(appContext: Context): EnvironmentHolder {
        val factoryDelegate: FactoryOfEnvironmentHolder = FactoryDelegateOfEnvironmentHolder(
            appContext = appContext,
        )
        val factoryDecorator: FactoryOfEnvironmentHolder = FactoryDecoratorOfEnvironmentHolder(
            appContext = appContext,
            factoryDelegate = factoryDelegate,
        )
        return factoryDecorator.create()
    }
}
