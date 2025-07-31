package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class AtmCardStatus(
    val nameFromNetworkCall: String,
    val analyticsValue: String,
) {

    ACTIVE(
        nameFromNetworkCall = "ACTIVA",
        analyticsValue = Constant.EMPTY_STRING,
    ),
    INACTIVE(
        nameFromNetworkCall = "INACTIVA",
        analyticsValue = Constant.EMPTY_STRING,
    ),
    LOCKED(
        nameFromNetworkCall = "BLOQUEADA",
        analyticsValue = Constant.EMPTY_STRING,
    ),
    PENDING(
        nameFromNetworkCall = "PENDIENTE",
        analyticsValue = Constant.EMPTY_STRING,
    ),
    NONE(
        nameFromNetworkCall = Constant.EMPTY_STRING,
        analyticsValue = Constant.EMPTY_STRING,
    );

    companion object {

        @JvmStatic
        fun identifyByName(name: String?): AtmCardStatus = when (name?.uppercase()) {
            ACTIVE.nameFromNetworkCall -> ACTIVE
            INACTIVE.nameFromNetworkCall -> INACTIVE
            LOCKED.nameFromNetworkCall -> LOCKED
            PENDING.nameFromNetworkCall -> PENDING
            else -> NONE
        }
    }
}
