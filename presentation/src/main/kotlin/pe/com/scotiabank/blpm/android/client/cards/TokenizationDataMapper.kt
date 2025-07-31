package pe.com.scotiabank.blpm.android.client.cards

import pe.com.scotiabank.blpm.android.client.model.cards.Tokenization
import pe.com.scotiabank.blpm.android.data.entity.cards.TokenizationEntity

fun transformTokenizationEntity(tokenizationEntity: TokenizationEntity?): Tokenization = Tokenization(
    eligible = tokenizationEntity?.eligible ?: false,
    institutionCode = tokenizationEntity?.institutionCode.orEmpty(),
    panId = tokenizationEntity?.panId.orEmpty(),
    tokenIds = tokenizationEntity?.tokenIds.orEmpty().filterNotNull()
)
