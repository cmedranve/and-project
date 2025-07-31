package pe.com.scotiabank.blpm.android.client.base.session.entities

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class SubProductType(val id: String, val productType: ProductType, val displayText: String) {

    ADS(
        id = "ADS",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    LD(
        id = "LD",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    PA(
        id = "PA",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    PL(
        id = "PL",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    DX(
        id = "DX",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    DC(
        id = "DC",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    RL(
        id = "RL",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    RP(
        id = "RP",
        productType = ProductType.LOAN,
        displayText = Constant.EMPTY_STRING,
    ),
    EL(
        id = "EL",
        productType = ProductType.CREDIT_CARD,
        displayText = Constant.EMPTY_STRING,
    ),
    PAYROLL(
        id = "PAYROLL",
        productType = ProductType.SAVING_ACCOUNT,
        displayText = Constant.EMPTY_STRING,
    ),
    FREE(
        id = "FREE",
        productType = ProductType.SAVING_ACCOUNT,
        displayText = Constant.EMPTY_STRING,
    ),
    POWER(
        id = "POWER",
        productType = ProductType.SAVING_ACCOUNT,
        displayText = Constant.EMPTY_STRING,
    ),
    TRAVEL(
        id = "TRAVEL",
        productType = ProductType.SAVING_ACCOUNT,
        displayText = Constant.EMPTY_STRING,
    ),
    SUPER(
        id = "SUPER",
        productType = ProductType.SAVING_ACCOUNT,
        displayText = Constant.EMPTY_STRING,
    ),
    DIGITAL(
        id = "DIGITAL",
        productType = ProductType.SAVING_ACCOUNT,
        displayText = Constant.EMPTY_STRING,
    ),
    NONE(
        id = "NONE",
        productType = ProductType.ACCOUNT,
        displayText = Constant.EMPTY_STRING,
    );

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): SubProductType = when (id?.uppercase()) {
            ADS.id -> ADS
            LD.id -> LD
            PA.id -> PA
            PL.id -> PL
            DX.id -> DX
            DC.id -> DC
            RL.id -> RL
            RP.id -> RP
            EL.id -> EL
            PAYROLL.id -> PAYROLL
            FREE.id -> FREE
            POWER.id -> POWER
            TRAVEL.id -> TRAVEL
            SUPER.id -> SUPER
            else -> NONE
        }
    }
}
