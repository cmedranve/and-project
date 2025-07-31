package pe.com.scotiabank.blpm.android.ui.list.items.tooltip

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import pe.com.scotiabank.blpm.android.ui.util.ContextUtil

internal inline fun attemptBindToolTip(
    entity: UiEntityOfToolTip?,
    binding: ViewBinding,
    setterCallback: (accessibilityText: String, headlineText: String, contentText: String, buttonText: String, fm: FragmentManager) -> Unit
) {
    val fragmentActivity: AppCompatActivity? = ContextUtil.findAppCompatActivity(binding.root.context)

    if (entity != null && fragmentActivity != null) {
        setterCallback.invoke(
            entity.accessibilityText,
            entity.headlineText,
            entity.contentText,
            entity.buttonText,
            fragmentActivity.supportFragmentManager
        )
    }
}
