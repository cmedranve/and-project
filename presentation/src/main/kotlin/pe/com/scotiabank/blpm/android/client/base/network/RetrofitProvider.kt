package pe.com.scotiabank.blpm.android.client.base.network

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

internal object RetrofitProvider {

    /**
     * Build Mapper for Jackson
     *
     * @return ObjectMapper
     */
    fun createConverterFactory(
        objectMapper: ObjectMapper
    ): Converter.Factory = JacksonConverterFactory.create(objectMapper)

    /**
     * Build Retrofit
     *
     * @return Retrofit
     */
    fun buildRetrofit(
        converterFactory: Converter.Factory,
        url: String,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addCallAdapterFactory(
            RxJava2CallAdapterFactory.createAsync()
        )
        .addConverterFactory(converterFactory)
        .baseUrl(url)
        .client(okHttpClient)
        .build()
}
