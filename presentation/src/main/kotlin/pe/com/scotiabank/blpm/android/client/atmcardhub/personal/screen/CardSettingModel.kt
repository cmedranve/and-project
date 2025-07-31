package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction

interface CardSettingModel : SuspendingFunction<String, Any> {

    val isMainOrJoinHolder: Boolean

    val isCardLocked: Boolean

    val isPurchasesDisabled: Boolean
}
