package pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown

import android.view.View
import android.widget.AdapterView
import androidx.core.util.Consumer
import com.scotiabank.canvascore.selectors.Dropdown

class ProxyOfDropdownCallbacks(private val onItemClicked: Consumer<Int>): Dropdown.Callbacks {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemClicked.accept(position)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {
        // no-op
    }
}
