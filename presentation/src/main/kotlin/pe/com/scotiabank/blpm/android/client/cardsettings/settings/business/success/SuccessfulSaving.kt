package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.success

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.calltoaction.CallToAction

enum class SuccessfulSaving(
    val id: Long,
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val callToActions: List<CallToAction>,
) {

    SUCCESS_DATA(
        id = randomLong(),
        imageRes = pe.com.scotiabank.blpm.android.ui.R.drawable.ic_complete_48px,
        titleRes = R.string.card_settings_saved_successful_title,
        descriptionRes = R.string.card_settings_saved_successful_description,
        callToActions = listOf(CallToAction.UNDERSTOOD_PRIMARY),
    )
}