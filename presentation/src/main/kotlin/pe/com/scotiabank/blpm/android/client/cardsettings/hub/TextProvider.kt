package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.res.Resources
import android.text.SpannableStringBuilder
import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R
import java.lang.ref.WeakReference

class TextProvider(private val weakResources: WeakReference<Resources?>) {

    val snackbarMessageForEditedCard: SpannableStringBuilder by lazy {
        buildSnackbarMessage(R.string.cards_settings_changes_snack_message)
    }

    val snackbarMessageForRegisteredTravel: SpannableStringBuilder by lazy {
        buildSnackbarMessage(R.string.cards_settings_travel_agent)
    }

    private fun buildSnackbarMessage(@StringRes textResId: Int): SpannableStringBuilder {
        val text: String = weakResources.get()?.getString(textResId).orEmpty()
        return SpannableStringBuilder.valueOf(text)
    }
}
