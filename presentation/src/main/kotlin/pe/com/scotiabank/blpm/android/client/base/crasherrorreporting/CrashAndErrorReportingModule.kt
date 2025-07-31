package pe.com.scotiabank.blpm.android.client.base.crasherrorreporting

import android.content.Context
import com.scotiabank.sdk.crasherrorreporting.converter.defaults.DefaultConverterForDeserializationErrorProvider
import com.scotiabank.sdk.crasherrorreporting.deserializer.ConverterFactoryDecorator
import com.scotiabank.sdk.crasherrorreporting.deserializer.DeserializationErrorProvider
import com.scotiabank.sdk.crasherrorreporting.network.ErrorInterceptor
import com.scotiabank.sdk.crasherrorreporting.network.NetworkErrorProvider
import com.scotiabank.sdk.crasherrorreporting.network.toRelativeUrl
import dagger.Module
import dagger.Provides
import pe.com.scotiabank.blpm.android.client.base.ApplicationScope
import pe.com.scotiabank.blpm.android.client.base.network.ExternalEnvironment
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.data.net.interceptor.ProxyOfErrorInterceptor
import retrofit2.Converter

@Module
class CrashAndErrorReportingModule {

    @Provides
    @ApplicationScope
    fun provideProxyOfErrorInterceptor(
        environmentHolder: EnvironmentHolder,
        errorInterceptor: ErrorInterceptor,
    ): ProxyOfErrorInterceptor {
        val environment: ExternalEnvironment = environmentHolder.environment
        return ProxyOfErrorInterceptor(errorInterceptor, environment.baseUrlOfApi)
    }
    @Provides
    @ApplicationScope
    fun provideNetworkErrorProvider(
        errorInterceptor: ErrorInterceptor,
    ): NetworkErrorProvider = errorInterceptor

    @Provides
    @ApplicationScope
    fun provideErrorInterceptor(
        environmentHolder: EnvironmentHolder,
        errorInterceptorMapper: ErrorInterceptorMapper,
    ): ErrorInterceptor {
        val environment: ExternalEnvironment = environmentHolder.environment
        return ErrorInterceptor(
            exceptionMapper = errorInterceptorMapper::map,
            errorResponseMapper = errorInterceptorMapper::map,
            relativeUrlMapper = { url -> url.toRelativeUrl(environment.baseUrlOfApi) }
        )
    }

    @Provides
    @ApplicationScope
    fun provideErrorInterceptorMapper(
        appContext: Context,
    ): ErrorInterceptorMapper = ErrorInterceptorMapper(appContext)

    @Provides
    @ApplicationScope
    fun provideConverterForDeserializationErrorProvider(
        deserializationErrorProvider: DeserializationErrorProvider,
    ): DefaultConverterForDeserializationErrorProvider {
        return DefaultConverterForDeserializationErrorProvider(deserializationErrorProvider)
    }

    @Provides
    @ApplicationScope
    fun provideDeserializationErrorProvider(
        converterFactoryDecorator: ConverterFactoryDecorator,
    ): DeserializationErrorProvider = converterFactoryDecorator

    @Provides
    @ApplicationScope
    fun provideConverterFactoryDecorator(
        defaultFactory: Converter.Factory,
    ): ConverterFactoryDecorator = ConverterFactoryDecorator(defaultFactory)
}
