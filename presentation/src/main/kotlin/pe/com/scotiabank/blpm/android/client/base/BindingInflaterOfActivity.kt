package pe.com.scotiabank.blpm.android.client.base

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

fun interface BindingInflaterOfActivity<B: ViewBinding> {

    fun inflate(inflater: LayoutInflater): B
}
