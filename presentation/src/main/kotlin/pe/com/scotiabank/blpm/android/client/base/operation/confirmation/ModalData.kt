package pe.com.scotiabank.blpm.android.client.base.operation.confirmation

import androidx.annotation.StringRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R

enum class ModalData(
    val id: Long,
    @StringRes val titleRes: Int,
    @StringRes val textBodyRes: Int,
    @StringRes val primaryButtonLabel: Int,
    @StringRes val secondaryButtonLabel: Int,
    val buttonDirectionColumn: Boolean,
) {

    CANCEL_TRANSFER(
        id = randomLong(),
        titleRes = R.string.cancel_transfer,
        textBodyRes = R.string.are_you_sure_to_cancel_this_transfer,
        primaryButtonLabel = R.string.yes,
        secondaryButtonLabel = R.string.no,
        buttonDirectionColumn = false,
    ),

    CANCEL_PAYMENT(
        id = randomLong(),
        titleRes = R.string.cancel_payment,
        textBodyRes = R.string.are_you_sure_to_cancel_this_payment,
        primaryButtonLabel = R.string.yes,
        secondaryButtonLabel = R.string.no,
        buttonDirectionColumn = false,
    ),

    CANCEL_DEFAULT(
        id = randomLong(),
        titleRes = R.string.cancel,
        textBodyRes = R.string.are_you_sure_to_cancel,
        primaryButtonLabel = R.string.yes,
        secondaryButtonLabel = R.string.no,
        buttonDirectionColumn = false,
    );
}
