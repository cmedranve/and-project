package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import android.content.res.Resources
import androidx.core.util.Consumer
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.Intention
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModel
import java.lang.ref.WeakReference

class DataOfMaxSelectionDialog(
    private val helper: HelperForMaxSelectionDialog,
    frequentOperationType: FrequentOperationType,
) {

    val message: CharSequence = helper.buildMessage(frequentOperationType)

    val textForPositiveButton: CharSequence
        get() = helper.textForPositiveButton

    val callbackForPositiveButton: Consumer<MyListViewModel>
        get() = helper.callbackForPositiveButton

    val callbackForDismiss: Consumer<MyListViewModel>
        get() = helper.callbackForDismiss

    val isCancelable: Boolean
        get() = helper.isCancelable
}

class HelperForMaxSelectionDialog(private val weakResources: WeakReference<Resources?>) {

    val textForPositiveButton: CharSequence by lazy {
        weakResources.get()
            ?.getString(R.string.accept)
            .orEmpty()
    }

    val callbackForPositiveButton: Consumer<MyListViewModel> by lazy {
        Consumer(::sendPositiveEvent)
    }

    val callbackForDismiss: Consumer<MyListViewModel> by lazy {
        Consumer(::sendDismissEvent)
    }

    val isCancelable: Boolean by lazy {
        false
    }

    private fun sendPositiveEvent(viewModel: MyListViewModel) {
        viewModel.receiveEvent(Intention.NOTIFY_OK_ABOUT_MAX_SELECTION)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun sendDismissEvent(viewModel: MyListViewModel) {
        // no-op
    }

    fun buildMessage(
        frequentOperationType: FrequentOperationType,
    ): CharSequence = weakResources.get()
        ?.getString(
            R.string.my_list_max_selection_rationale,
            frequentOperationType.displayText.lowercase(),
        )
        .orEmpty()
}