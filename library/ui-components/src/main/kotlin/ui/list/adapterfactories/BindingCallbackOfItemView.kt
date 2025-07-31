package pe.com.scotiabank.blpm.android.ui.list.adapterfactories

import android.view.View
import androidx.viewbinding.ViewBinding

fun interface BindingCallbackOfItemView<B: ViewBinding> {

    fun bind(root: View): B
}
