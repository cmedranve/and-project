package pe.com.scotiabank.blpm.android.client.base.operation.completion

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

    GENERIC_ERROR(
        id = randomLong(),
        titleRes = ResourcesCompat.ID_NULL,
        textBodyRes = R.string.exception_message_generic,
        primaryButtonLabel = R.string.accept,
        secondaryButtonLabel = ResourcesCompat.ID_NULL,
        buttonDirectionColumn = true,
    );
}
