package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import android.content.res.Resources
import androidx.core.util.Consumer
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.Intention
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModel
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import java.lang.ref.WeakReference

class DataOfDeletionDialog(
    private val helper: HelperForDeletionDialog,
    frequentOperation: FrequentOperationModel,
) {

    val message: CharSequence = helper.buildMessage(frequentOperation)

    val textForPositiveButton: CharSequence
        get() = helper.textForPositiveButton

    val callbackForPositiveButton: Consumer<MyListViewModel>
        get() = helper.callbackForPositiveButton

    val textForNegativeButton: CharSequence
        get() = helper.textForNegativeButton

    val callbackForNegativeButton: Consumer<MyListViewModel>
        get() = helper.callbackForNegativeButton
}

class HelperForDeletionDialog(private val weakResources: WeakReference<Resources?>) {

    val textForPositiveButton: CharSequence by lazy {
        weakResources.get()
            ?.getString(R.string.delete)
            .orEmpty()
    }

    val callbackForPositiveButton: Consumer<MyListViewModel> by lazy {
        Consumer(::sendPositiveEvent)
    }

    val textForNegativeButton: CharSequence by lazy {
        weakResources.get()
            ?.getString(R.string.cancel)
            .orEmpty()
    }

    val callbackForNegativeButton: Consumer<MyListViewModel> by lazy {
        Consumer(::sendNegativeEvent)
    }

    private fun sendPositiveEvent(viewModel: MyListViewModel) {
        viewModel.receiveEvent(Intention.START_DELETING_OPERATION)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun sendNegativeEvent(viewModel: MyListViewModel) {
        // no-op
    }

    fun buildMessage(
        frequentOperation: FrequentOperationModel,
    ): CharSequence = weakResources.get()
        ?.getString(
            R.string.do_you_want_to_delete_this_frequent_payment_from_my_list,
            frequentOperation.title,
        )
        .orEmpty()
}