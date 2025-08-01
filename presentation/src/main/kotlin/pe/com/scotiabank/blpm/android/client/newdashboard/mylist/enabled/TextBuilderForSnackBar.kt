package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import android.content.res.Resources
import android.text.SpannableStringBuilder
import androidx.annotation.PluralsRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import java.lang.ref.WeakReference

class TextBuilderForSnackBar(
    private val appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
) {

    private val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(Constant.EMPTY_STRING)

    fun buildTextForAdding(operations: List<FrequentOperationModel>): SpannableStringBuilder {
        val boldText: CharSequence = buildBoldTextOf(operations) ?: return empty
        val fullText: CharSequence = weakResources.get()
            ?.getString(R.string.my_list_frequent_payments_news, boldText)
            ?: return empty
        return SpannableStringBuilder
            .valueOf(fullText)
            .setTypefaceSpan(appModel.boldTypeface, boldText)
    }

    private fun buildBoldTextOf(operations: List<FrequentOperationModel>): CharSequence? {

        if (operations.size != 1) return findQuantityForBoldText(operations.size)

        return operations.firstOrNull()?.title ?: findQuantityForBoldText(operations.size)
    }

    private fun findQuantityForBoldText(quantity: Int): CharSequence? {
        @PluralsRes val pluralsResId: Int = R.plurals.my_list_snackbar_payment_text_bold
        return weakResources.get()?.getQuantityString(pluralsResId, quantity, quantity)
    }

    fun buildTextForDeletion(operation: FrequentOperationModel): SpannableStringBuilder {
        val boldText: CharSequence = operation.title ?: return empty
        val fullText: CharSequence = weakResources.get()
            ?.getString(R.string.my_list_frequent_payments_deleted, boldText)
            ?: return empty
        return SpannableStringBuilder
            .valueOf(fullText)
            .setTypefaceSpan(appModel.boldTypeface, boldText)
    }

    fun buildTextForEditing(): SpannableStringBuilder {
        val fullText: CharSequence = weakResources.get()
            ?.getString(R.string.my_list_frequent_payments_edited)
            ?: return empty
        return SpannableStringBuilder
            .valueOf(fullText)
    }
}