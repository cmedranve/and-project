package pe.com.scotiabank.blpm.android.ui.util

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

internal object ContextUtil {

    @JvmStatic
    fun findAppCompatActivity(context: Context): AppCompatActivity? {
        if (context is AppCompatActivity) return context
        if (context !is ContextWrapper) return null
        return findAppCompatActivity(context.baseContext)
    }
}
