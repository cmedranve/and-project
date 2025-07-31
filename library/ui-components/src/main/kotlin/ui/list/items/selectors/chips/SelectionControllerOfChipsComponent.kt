package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import androidx.core.util.Consumer
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.EventOfSelectionController
import java.util.concurrent.ConcurrentHashMap

class SelectionControllerOfChipsComponent<D : Any>(
    private val instanceReceiver: InstanceReceiver
) {

    private var componentEntity: UiEntityOfChipsComponent<D>? = null
    private var defaultChipEntity: UiEntityOfChip<D>? = null
    private val chipEntitiesByText: Map<String, UiEntityOfChip<D>>?
        get() = componentEntity?.chipEntitiesByChipText

    private val selectedTextBySingleKey: MutableMap<Int, String?> = ConcurrentHashMap()
    private val textOfSelectedChip: String
        get() = selectedTextBySingleKey[SINGLE_KEY].orEmpty()

    val selectedChip: UiEntityOfChip<D>?
        get() = chipEntitiesByText?.get(textOfSelectedChip)

    private val isInitializing: Boolean
        get() = selectedChip == null && defaultChipEntity == null

    internal val onChipClicked: Consumer<String?> by lazy {
        Consumer(::handleChipClicked)
    }

    fun setComponentEntity(componentEntity: UiEntityOfChipsComponent<D>) {
        this.componentEntity = componentEntity
    }

    fun setDefaultChip(chipEntity: UiEntityOfChip<D>) {
        val isTheSame: Boolean = isTheSameAsTheSelectedChip(chipEntity)
        if (isTheSame) return

        val chipEntityFound: UiEntityOfChip<D> = chipEntitiesByText?.get(chipEntity.text) ?: return

        val isInitializing: Boolean = isInitializing
        selectedChip?.let(::dropFromSelection)
        addToSelection(chipEntityFound)
        defaultChipEntity = chipEntityFound
        if (isInitializing) return

        instanceReceiver.receive(EventOfSelectionController.NEW_DEFAULT)
    }

    private fun isTheSameAsTheSelectedChip(chipEntity: UiEntityOfChip<D>): Boolean {
        val idOfSelectedChip: Long? = selectedChip?.id
        return idOfSelectedChip != null && chipEntity.id == idOfSelectedChip
    }

    private fun dropFromSelection(previousSelectedChip: UiEntityOfChip<D>) {
        previousSelectedChip.mutableIsChecked = false
    }

    private fun addToSelection(chipEntity: UiEntityOfChip<D>) {
        chipEntity.mutableIsChecked = true
        selectedTextBySingleKey[SINGLE_KEY] = chipEntity.text
    }

    private fun handleChipClicked(nullableChipText: String?) {
        val chipText: String = nullableChipText ?: return
        if (chipText.contentEquals(textOfSelectedChip)) return
        val chipEntity: UiEntityOfChip<D> = chipEntitiesByText?.get(chipText) ?: return

        selectedChip?.let(::dropFromSelection)
        addToSelection(chipEntity)
        instanceReceiver.receive(chipEntity)
    }

    fun reset() {
        if (isInitializing) return
        val isTheSame: Boolean = defaultChipEntity?.let(::isTheSameAsTheSelectedChip) ?: false
        if (isTheSame) return

        selectedChip?.let(::dropFromSelection)
        defaultChipEntity?.let(::addToSelection)
        instanceReceiver.receive(EventOfSelectionController.RESET)
    }

    companion object {

        private const val SINGLE_KEY = 0
    }
}
