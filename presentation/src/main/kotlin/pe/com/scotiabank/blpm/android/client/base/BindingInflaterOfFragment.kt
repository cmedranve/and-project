package pe.com.scotiabank.blpm.android.client.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

fun interface BindingInflaterOfFragment<B: ViewBinding> {

    fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean): B
}
