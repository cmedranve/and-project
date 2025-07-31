package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class AtmCardForm(val id: String, val displayText: String) {

    PHYSICAL(id = "F", displayText = "Física"),
    DIGITAL(id = "D", displayText = "Digital"),
    NONE(id = Constant.EMPTY_STRING, displayText = Constant.EMPTY_STRING);

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): AtmCardForm = when (id?.uppercase()) {
            PHYSICAL.id -> PHYSICAL
            DIGITAL.id -> DIGITAL
            else -> NONE
        }
    }
}
