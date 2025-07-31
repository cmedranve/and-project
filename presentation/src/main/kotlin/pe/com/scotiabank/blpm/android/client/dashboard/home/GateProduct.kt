package pe.com.scotiabank.blpm.android.client.dashboard.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R

enum class GateProduct(
    @DrawableRes val idRes: Int,
    @StringRes val description: Int
) {

    HUB(
        idRes = com.scotiabank.icons.illustrative.R.drawable.ic_debit_card_outlined_multicoloured_24,
        description = R.string.my_cards
    ),

    ACQUIRE_PRODUCT(
        idRes = R.drawable.ic_marketplace,
        description = R.string.operation_gate_title
    ),
}