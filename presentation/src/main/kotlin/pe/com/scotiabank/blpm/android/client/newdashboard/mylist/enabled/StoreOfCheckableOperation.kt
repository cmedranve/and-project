package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import java.util.function.BiFunction

class StoreOfCheckableOperation(
    receiver: InstanceReceiver,
    private val matcher: OperationMatcher,
    private val converter: BiFunction<FrequentOperationModel, Long, UiEntityOfCheckableButton<FrequentOperationModel>>,
) : HolderOfCheckBoxController, FrequentOperationService {

    override val controller: ControllerOfMultipleSelection<FrequentOperationModel> = ControllerOfMultipleSelection(
        instanceReceiver = receiver,
    )

    private val _itemEntities: MutableList<UiEntityOfCheckableButton<FrequentOperationModel>> = mutableListOf()
    val itemEntities: List<UiEntityOfCheckableButton<FrequentOperationModel>>
        get() = _itemEntities
    override val quantity: Int by _itemEntities::size

    override fun add(frequentOperation: FrequentOperationModel) {
        val newEntity: UiEntityOfCheckableButton<FrequentOperationModel> = converter.apply(
            frequentOperation,
            randomLong(),
        )
        _itemEntities.add(newEntity)
    }

    override fun edit(frequentOperation: FrequentOperationModel) {
        val indexOfOldEntity: Int = _itemEntities
            .indexOfFirst { entity -> matcher.isMatching(entity.data, frequentOperation) }
        if (NOT_FOUND == indexOfOldEntity) return

        val oldEntity: UiEntityOfCheckableButton<FrequentOperationModel> = _itemEntities[indexOfOldEntity]
        val newEntity: UiEntityOfCheckableButton<FrequentOperationModel> = converter.apply(
            frequentOperation,
            oldEntity.id,
        )
        _itemEntities[indexOfOldEntity] = newEntity

        if (oldEntity.isChecked) {
            controller.dropFromSelection(oldEntity)
            controller.addToSelection(newEntity)
        }
    }

    override fun remove(frequentOperation: FrequentOperationModel) {
        val oldEntity: UiEntityOfCheckableButton<FrequentOperationModel> = _itemEntities
            .firstOrNull { entity -> matcher.isMatching(entity.data, frequentOperation) }
            ?: return

        _itemEntities.remove(oldEntity)

        if (oldEntity.isChecked) {
            controller.dropFromSelection(oldEntity)
        }
    }

    override fun clear() {
        _itemEntities.clear()
    }

    companion object {

        private val NOT_FOUND: Int
            get() = -1
    }
}