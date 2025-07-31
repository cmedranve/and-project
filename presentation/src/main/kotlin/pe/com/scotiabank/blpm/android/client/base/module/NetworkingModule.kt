package pe.com.scotiabank.blpm.android.client.base.module

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import pe.com.scotiabank.blpm.android.client.base.ApplicationScope
import pe.com.scotiabank.blpm.android.client.base.network.RetrofitProvider
import retrofit2.Converter

@Module
class NetworkingModule {

    @Provides
    @ApplicationScope
    fun provideConverterFactory(
        objectMapper: ObjectMapper
    ): Converter.Factory = RetrofitProvider.createConverterFactory(objectMapper)
}
