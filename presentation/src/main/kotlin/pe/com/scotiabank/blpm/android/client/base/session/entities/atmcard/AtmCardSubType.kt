package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class AtmCardSubType(
    val id: String,
    val atmCardType: AtmCardType,
    val atmCardForm: AtmCardForm,
) {

    PHYSICAL_DEBIT(
        id = Constant.TDF,
        atmCardType = AtmCardType.DEBIT,
        atmCardForm = AtmCardForm.PHYSICAL,
    ) {

        override val displayText: String
            get() = atmCardType.displayText + Constant.SPACE_WHITE + atmCardForm.displayText

        override val analyticsValue: String
            get() = atmCardType.analyticsValue

        override val sectionDetailAnalyticsValue: String
            get() = "tdebito-fisica"
    },
    DIGITAL_DEBIT(
        id = Constant.TDD,
        atmCardType = AtmCardType.DEBIT,
        atmCardForm = AtmCardForm.DIGITAL,
    ) {

        override val displayText: String
            get() = atmCardType.displayText + Constant.SPACE_WHITE + atmCardForm.displayText

        override val analyticsValue: String
            get() = atmCardType.analyticsValue

        override val sectionDetailAnalyticsValue: String
            get() = "tdebito-digital"
    },
    PHYSICAL_CREDIT(
        id = Constant.TC,
        atmCardType = AtmCardType.CREDIT,
        atmCardForm = AtmCardForm.NONE,
    ) {

        override val displayText: String
            get() = atmCardType.displayText

        override val analyticsValue: String
            get() = atmCardType.analyticsValue

        override val sectionDetailAnalyticsValue: String
            get() = "tcredito"
    },
    NONE(
        id = "NONE",
        atmCardType = AtmCardType.NONE,
        atmCardForm = AtmCardForm.NONE,
    ) {

        override val displayText: String
            get() = Constant.EMPTY_STRING

        override val analyticsValue: String
            get() = atmCardType.analyticsValue

        override val sectionDetailAnalyticsValue: String
            get() = Constant.EMPTY_STRING
    };

    abstract val displayText: String
    abstract val analyticsValue: String
    abstract val sectionDetailAnalyticsValue: String

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): AtmCardSubType = when (id?.uppercase()) {
            PHYSICAL_DEBIT.id, Constant.TD -> PHYSICAL_DEBIT
            DIGITAL_DEBIT.id -> DIGITAL_DEBIT
            PHYSICAL_CREDIT.id -> PHYSICAL_CREDIT
            else -> NONE
        }
    }
}
