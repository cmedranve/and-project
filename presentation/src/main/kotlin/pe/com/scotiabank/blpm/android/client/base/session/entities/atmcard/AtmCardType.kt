package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class AtmCardType(
    val id: String,
    val displayText: String,
    val analyticsValue: String,
    val nameFromNetworkCall: String,
) {

    CREDIT(
        id = Constant.TC,
        displayText = "Crédito",
        analyticsValue = "tarjeta-de-credito",
        nameFromNetworkCall = "CREDIT_CARD",
    ),
    DEBIT(
        id = Constant.TD,
        displayText = "Débito",
        analyticsValue = "tarjeta-de-debito",
        nameFromNetworkCall = "DEBIT_CARD",
    ),
    NONE(
        id = "NONE",
        displayText = Constant.EMPTY_STRING,
        analyticsValue = Constant.EMPTY_STRING,
        nameFromNetworkCall = Constant.EMPTY_STRING,
    );

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): AtmCardType = when (id?.uppercase()) {
            CREDIT.id -> CREDIT
            DEBIT.id -> DEBIT
            else -> NONE
        }

        @JvmStatic
        fun identifyByName(name: String?): AtmCardType = when (name?.uppercase()) {
            CREDIT.nameFromNetworkCall -> CREDIT
            DEBIT.nameFromNetworkCall -> DEBIT
            else -> NONE
        }
    }
}
