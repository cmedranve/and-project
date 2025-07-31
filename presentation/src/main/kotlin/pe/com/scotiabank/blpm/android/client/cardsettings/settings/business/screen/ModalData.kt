package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
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

    SAVE_CHANGES(
        id = randomLong(),
        titleRes = R.string.btn_save_changes,
        textBodyRes = R.string.card_settings_save_changes_modal_body,
        primaryButtonLabel = R.string.save,
        secondaryButtonLabel = R.string.my_account_log_out,
        buttonDirectionColumn = true,
    ),

    ERROR(
        id = randomLong(),
        titleRes = R.string.card_settings_error_title,
        textBodyRes = R.string.notification_retry,
        primaryButtonLabel = R.string.accept,
        secondaryButtonLabel = ResourcesCompat.ID_NULL,
        buttonDirectionColumn = true,
    );
}