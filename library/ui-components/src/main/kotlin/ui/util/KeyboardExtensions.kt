package pe.com.scotiabank.blpm.android.ui.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment

/**
 * If possible, don't rely on using View.showKeyboard() since the keyboard may be part of a
 * different Window instance in a Dialog or a Fragment
 * */
fun View.showKeyboard() = doOnLayout { context.findActivity()?.window?.showKeyboard() }

/**
 * If possible, don't rely on using View.hideKeyboard() since the keyboard may be part of a
 * different Window instance in a Dialog or a Fragment
 * */
fun View.hideKeyboard() = doOnLayout { context.findActivity()?.window?.hideKeyboard() }

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext as? Activity
    else -> null
}

fun Dialog.showKeyboard() = window?.decorView?.doOnLayout { window?.showKeyboard() }
fun Dialog.hideKeyboard() = window?.decorView?.doOnLayout { window?.hideKeyboard() }

fun Fragment.showKeyboard() = activity?.showKeyboard()
fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Activity.showKeyboard() = window.decorView.doOnLayout { window.showKeyboard() }
fun Activity.hideKeyboard() = window.decorView.doOnLayout { window.hideKeyboard() }

private fun Window.showKeyboard() {
    val insetsController: WindowInsetsControllerCompat = WindowCompat.getInsetsController(
        this,
        decorView
    )
    @InsetsType val ime: Int = WindowInsetsCompat.Type.ime()
    insetsController.show(ime)
}

private fun Window.hideKeyboard() {
    val insetsController: WindowInsetsControllerCompat = WindowCompat.getInsetsController(
        this,
        decorView
    )
    @InsetsType val ime: Int = WindowInsetsCompat.Type.ime()
    insetsController.hide(ime)
}
