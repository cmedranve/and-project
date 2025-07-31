package pe.com.scotiabank.blpm.android.client.base.parser

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import pe.com.scotiabank.blpm.android.data.deserializers.Loop2PayConfirmationDeserializer
import pe.com.scotiabank.blpm.android.data.entity.loop2pay.Loop2PayBaseConfirmationEntity

internal object ObjectMapperProvider {

    /**
     * Build Mapper for Jackson
     *
     * @return ObjectMapper
     */
    fun buildObjectMapper(): ObjectMapper = JsonMapper
        .builder(
            buildJsonFactory()
        )
        .configure(SerializationFeature.CLOSE_CLOSEABLE, true)
        .addModule(
            buildKotlinModule()
        )
        .addModule(
            createSimpleModule()
        )
        .build()

    /**
     * Build Json Factory for Jackson
     *
     * @return JsonFactory
     */
    private fun buildJsonFactory(): JsonFactory = JsonFactory
        .builder()
        .configure(JsonFactory.Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING, false)
        .build()

    /**
     * Create SimpleModule for ObjectMapper
     *
     * @return ObjectMapper
     */
    private fun buildKotlinModule(): KotlinModule = KotlinModule.Builder()
        .build()

    /**
     * Create SimpleModule for ObjectMapper
     *
     * @return ObjectMapper
     */
    private fun createSimpleModule() = SimpleModule()
        .addDeserializer(
            Loop2PayBaseConfirmationEntity::class.java,
            Loop2PayConfirmationDeserializer()
        )
}
