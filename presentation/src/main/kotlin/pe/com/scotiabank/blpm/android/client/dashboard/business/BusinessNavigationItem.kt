package pe.com.scotiabank.blpm.android.client.dashboard.business

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil

enum class BusinessNavigationItem(
    val idRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val title: Int,
    val templateKey: String,
) {

    HOME(
        idRes = R.drawable.ic_tab_home,
        iconRes = R.drawable.ic_tab_home,
        title = R.string.tab_home,
        templateKey = TemplatesUtil.HOME_KEY
    ),
    CONTACT(
        idRes = R.drawable.ic_tab_contact_pay,
        iconRes = R.drawable.ic_tab_contact_pay,
        title = R.string.tab_contact_pay,
        templateKey = TemplatesUtil.CONTACT_PAYMENT_KEY
    ),
    MY_ACCOUNT(
        idRes = R.drawable.ic_tab_my_account,
        iconRes = R.drawable.ic_tab_my_account,
        title = R.string.tab_my_account,
        templateKey = TemplatesUtil.MORE_KEY
    )
}
