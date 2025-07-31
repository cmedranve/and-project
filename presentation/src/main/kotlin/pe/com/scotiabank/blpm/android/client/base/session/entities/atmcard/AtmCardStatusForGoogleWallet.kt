package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class AtmCardStatusForGoogleWallet(
    val analyticsValue: String,
) {

    TOKENIZED(
        analyticsValue = Constant.EMPTY_STRING,
    ),
    ELIGIBLE(
        analyticsValue = Constant.EMPTY_STRING,
    ),
    NONE(
        analyticsValue = Constant.EMPTY_STRING,
    );
}
