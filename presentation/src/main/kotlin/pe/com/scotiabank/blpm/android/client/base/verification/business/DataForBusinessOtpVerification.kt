package pe.com.scotiabank.blpm.android.client.base.verification.business

import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.verification.TransactionType

class DataForBusinessOtpVerification(
    @StringRes val titleResId: Int = R.string.my_account_digital_key,
    val transactionId: String,
    val transactionType: TransactionType,
)
