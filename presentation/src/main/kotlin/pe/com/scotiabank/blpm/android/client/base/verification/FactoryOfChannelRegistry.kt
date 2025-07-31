package pe.com.scotiabank.blpm.android.client.base.verification

import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.model.FactorModel
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.Channel
import pe.com.scotiabank.blpm.android.client.nosession.shared.channel.ChannelRegistry

class FactoryOfChannelRegistry {

    private val channelsByFactorType: Map<String, Channel> by lazy {
        mapOf(
            Channel.PHONE_FOR_PLIN_DIGITAL_KEY.typeForPeruApiCall to Channel.PHONE_FOR_PLIN_DIGITAL_KEY,
            Channel.EMAIL_FOR_PLIN_DIGITAL_KEY.typeForPeruApiCall to Channel.EMAIL_FOR_PLIN_DIGITAL_KEY
        )
    }

    fun createFrom(appModel: AppModel): ChannelRegistry {

        val channelValuesById: MutableMap<Long, CharArray> = mutableMapOf()

        for (factor in appModel.profile.factorModels) {
            val channel: Channel = channelsByFactorType[factor.type] ?: continue
            channelValuesById[channel.id] = factor.value.toCharArray()
        }

        val defaultType: String = appModel.profile.factorModels.firstOrNull(::isDefault)
            ?.type
            ?.uppercase()
            .orEmpty()

        val default: Channel = channelsByFactorType
            .values
            .first { channel -> channel.typeForPeruApiCall.uppercase().contentEquals(defaultType) }

        return ChannelRegistry(
            default = default,
            channelValuesById = channelValuesById
        )
    }

    private fun isDefault(factor: FactorModel): Boolean = factor.isDefaultAuth
}
