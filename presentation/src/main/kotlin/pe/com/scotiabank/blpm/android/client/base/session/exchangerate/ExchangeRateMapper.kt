package pe.com.scotiabank.blpm.android.client.base.session.exchangerate

import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.ExchangeRateEntity

object ExchangeRateMapper {

    @JvmStatic
    fun transform(entities: List<ExchangeRateEntity?>): List<ExchangeRate> = entities
        .filterNotNull()
        .map(::transform)

    @JvmStatic
    fun transform(entity: ExchangeRateEntity): ExchangeRate = ExchangeRate(
        type = entity.type ?: Constant.NONE,
        value = entity.value,
    )
}
