package pe.com.scotiabank.blpm.android.client.base.calltoaction

import androidx.annotation.StringRes
import com.scotiabank.canvascore.buttons.CanvasButton
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY

enum class CallToAction(
    val id: Long,
    @StringRes val buttonLabel: Int,
    val type: Int,
    val analyticLabel: String,
) {

    LOOK_FOR_NEAR_BRANCH_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.credit_card_error_find_agency,
        type = CanvasButton.PRIMARY,
        analyticLabel = "buscar-agencia-mas-cercana",
    ),

    LOOK_FOR_BRANCHES_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.contact_payment_restriction_button,
        type = CanvasButton.PRIMARY,
        analyticLabel = "buscar-agencias",
    ),

    CONFIRM_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.confirm,
        type = CanvasButton.PRIMARY,
        analyticLabel = "confirmar",
    ),

    CONTINUE_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.btn_continue,
        type = CanvasButton.PRIMARY,
        analyticLabel = "continuar",
    ),

    UNDERSTOOD_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.understood,
        type = CanvasButton.PRIMARY,
        analyticLabel = "entendido",
    ),

    GO_TO_HOME_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.go_home,
        type = CanvasButton.PRIMARY,
        analyticLabel = "ir-al-inicio",
    ),

    GO_TO_HOME_SECONDARY(
        id = randomLong(),
        buttonLabel = R.string.go_home,
        type = CanvasButton.SECONDARY,
        analyticLabel = "ir-al-inicio",
    ),

    GO_BACK_TO_MY_PAYMENTS_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.contact_pay_go_back_to_my_payments,
        type = CanvasButton.PRIMARY,
        analyticLabel = "volver-a-mis-cobros",
    ),

    SEE_MY_CREDIT_CARD_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.credit_card_show_detail,
        type = CanvasButton.PRIMARY,
        analyticLabel = "ver-mi-tarjeta",
    ),

    SEE_MY_CARD_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.see_card,
        type = CanvasButton.PRIMARY,
        analyticLabel = "ver-tarjeta",
    ),

    TRY_AGAIN_ERASER_FLOW_SECONDARY(
        id = randomLong(),
        buttonLabel = R.string.error_button_try_again,
        type = CanvasButton.SECONDARY,
        analyticLabel = String.EMPTY,
    ),

    GO_TO_ABOUT_ERASER_FLOW_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.eraser_button_no_purchase,
        type = CanvasButton.PRIMARY,
        analyticLabel = String.EMPTY,
    ),

    GO_TO_ABOUT_ERASER_FLOW_SECONDARY(
        id = randomLong(),
        buttonLabel = R.string.eraser_button_no_purchase,
        type = CanvasButton.SECONDARY,
        analyticLabel = String.EMPTY,
    ),

    TRY_AGAIN_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.try_again,
        type = CanvasButton.PRIMARY,
        analyticLabel = String.EMPTY,
    ),

    EXIT_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.exit,
        type = CanvasButton.PRIMARY,
        analyticLabel = String.EMPTY,
    ),

    GO_TO_PAN_PIN(
        id = randomLong(),
        buttonLabel = R.string.idv_error_fallback_lbl,
        type = CanvasButton.PRIMARY,
        analyticLabel = String.EMPTY,
    ),

    CREATE_DIGITAL_ACCOUNT(
        id = randomLong(),
        buttonLabel = R.string.create_digital_account,
        type = CanvasButton.PRIMARY,
        analyticLabel = "crear-una-cuenta-digital",
    ),

    GO_TO_ADD_MONEY_TO_GOAL(
        id = randomLong(),
        buttonLabel = R.string.add_money_to_my_goal,
        type = CanvasButton.PRIMARY,
        analyticLabel = "agregar-dinero-a-mi-meta",
    ),

    GO_TO_GOAL_DETAIL_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.see_my_goal,
        type = CanvasButton.PRIMARY,
        analyticLabel = "ver-mi-meta",
    ),

    GO_TO_GOAL_DETAIL_SECONDARY(
        id = randomLong(),
        buttonLabel = R.string.see_my_goal,
        type = CanvasButton.SECONDARY,
        analyticLabel = "ver-mi-meta",
    ),

    SHOW_MY_QR_SECONDARY(
        id = randomLong(),
        buttonLabel = R.string.contact_pay_affiliation_success_show_my_qr,
        type = CanvasButton.SECONDARY,
        analyticLabel = String.EMPTY,
    ),

    SHARE_MY_QR_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.contact_pay_share_my_qr,
        type = CanvasButton.PRIMARY,
        analyticLabel = String.EMPTY,
    ),

    PAY_WITH_QR_SECONDARY(
        id = randomLong(),
        buttonLabel = R.string.contact_pay_pay_with_qr,
        type = CanvasButton.SECONDARY,
        analyticLabel = String.EMPTY,
    ),

    START_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.idv_start_verification_start_text,
        type = CanvasButton.PRIMARY,
        analyticLabel = String.EMPTY,
    ),

    WHAT_HAPPENED_SECONDARY(
        id = randomLong(),
        buttonLabel = R.string.what_happened,
        type = CanvasButton.SECONDARY,
        analyticLabel = String.EMPTY,
    ),

    ALLOW_PRIMARY(
        id = randomLong(),
        buttonLabel = R.string.allow,
        type = CanvasButton.PRIMARY,
        analyticLabel = String.EMPTY,
    ),
}
