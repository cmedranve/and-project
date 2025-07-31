package pe.com.scotiabank.blpm.android.client.dashboard.person

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil

enum class PersonNavigationItem(
    val id: Long,
    val idRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val title: Int,
    val templateKey: String,
) {

    HOME(
        id = randomLong(),
        idRes = R.drawable.ic_tab_home,
        iconRes = R.drawable.ic_tab_home,
        title = R.string.tab_home,
        templateKey = TemplatesUtil.HOME_KEY
    ),

    MY_LIST(
        id = randomLong(),
        idRes = R.drawable.ic_tab_my_list,
        iconRes = R.drawable.ic_tab_my_list,
        title = R.string.tab_my_list,
        templateKey = TemplatesUtil.MY_LIST_KEY
    ),

    PLIN(
        id = randomLong(),
        idRes = R.drawable.ic_tab_plin,
        iconRes = R.drawable.ic_tab_plin,
        title = R.string.tab_plin,
        templateKey = TemplatesUtil.CONTACTS_KEY
    ),

    NOTIFICACIONS(
        id = randomLong(),
        idRes = R.drawable.ic_tab_notices,
        iconRes = R.drawable.ic_tab_notices,
        title = R.string.tab_notices,
        templateKey = TemplatesUtil.NOTIFICATIONS_KEY
    ),

    MY_ACCOUNT(
        id = randomLong(),
        idRes = R.drawable.ic_tab_my_account,
        iconRes = R.drawable.ic_tab_my_account,
        title = R.string.tab_my_account,
        templateKey = TemplatesUtil.MORE_KEY
    ),
}