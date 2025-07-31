package pe.com.scotiabank.blpm.android.client.base.contentprovider

enum class SecuredDirectoryRegistry(val dirName: String, val securedDirPath: String) {

    STATEMENTS(dirName = "account_statements", securedDirPath = "secured_dir_of_statements"),
    SCHEDULES(dirName = "payment_schedules", securedDirPath = "secured_dir_of_schedules"),
    VOUCHERS(dirName = "vouchers", securedDirPath = "secured_dir_of_vouchers")
}
