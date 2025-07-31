package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog

import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.exception.ExceptionWithResource

fun filterInExceptionOnNoAccounts(instance: ExceptionWithResource): Boolean {
    return R.string.exception_message_no_accounts == instance.messageResource
}
