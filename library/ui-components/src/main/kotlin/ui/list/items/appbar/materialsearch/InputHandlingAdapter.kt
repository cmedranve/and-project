package pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch

import android.text.Editable
import android.text.TextWatcher
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.util.Constant

class InputHandlingAdapter(
    private val entity: UiEntityOfMaterialSearch,
    private val textSupplying: Supplier<CharSequence>,
): TextWatcher {

    private val receiver: InstanceReceiver?
        get() = entity.receiver

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //not required
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val isInitialState = s.isNullOrBlank() && before == Constant.ZERO && count == Constant.ZERO
        if (isInitialState) return
        entity.text = textSupplying.get()
        receiver?.receive(entity)
    }

    override fun afterTextChanged(s: Editable?) {
        //not required
    }
}
