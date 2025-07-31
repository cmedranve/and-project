package pe.com.scotiabank.blpm.android.ui.list.items.navigation

import androidx.annotation.IdRes
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.items.badge.UiEntityOfBadge

class ComposerOfNavigation(
    private val coordinatorId: Long,
    private val selectedItemId: Long,
    private val receiver: InstanceReceiver,
) : NavigationController {

    private val entity: UiEntityOfNavigation by lazy {
        UiEntityOfNavigation(
            _itemEntitiesById = LinkedHashMap(),
            selectedItemId = selectedItemId,
            receiver = receiver,
            id = coordinatorId,
        )
    }
    private val itemEntitiesById: LinkedHashMap<Long, UiEntityOfNavigationItem> by entity::_itemEntitiesById

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompoundOfSingle<UiEntityOfNavigation> = UiCompoundOfSingle(entity, visibilitySupplier)

    override fun addItem(
        id: Long,
        @IdRes idRes: Int,
        badgeLabel: String?,
        badgeNumber: Int?,
        badgeMaxNumber: Int,
        isBadgeVisible: Boolean,
        title: CharSequence,
        iconRes: Int,
        data: Any?,
        isItemVisible: Boolean
    ) {
        val badgeEntity = UiEntityOfBadge(
            label = badgeLabel,
            number = badgeNumber,
            maxNumber = badgeMaxNumber,
            isVisible = isBadgeVisible,
        )
        val itemEntity = UiEntityOfNavigationItem(
            idRes = idRes,
            badgeEntity = badgeEntity,
            title = title,
            iconRes = iconRes,
            data = data,
            isVisible = isItemVisible,
            id = id,
        )
        itemEntitiesById[id] = itemEntity
    }

    override fun setSelectedItem(id: Long) {
        entity.selectedItemId = id
        showItem(id)
    }

    override fun showItem(id: Long) {
        val itemEntity: UiEntityOfNavigationItem = itemEntitiesById[id] ?: return
        itemEntity.isVisible = true
    }

    override fun hideItem(id: Long) {
        val itemEntity: UiEntityOfNavigationItem = itemEntitiesById[id] ?: return
        itemEntity.badgeEntity.label = null
        itemEntity.badgeEntity.number = null
        itemEntity.badgeEntity.isVisible = false
    }

    override fun editBadgeLabel(id: Long, badgeLabel: String?) {
        val badgeEntity: UiEntityOfBadge = findBadgeEntity(id) ?: return
        badgeEntity.label = badgeLabel
        badgeEntity.number = null
    }

    private fun findBadgeEntity(id: Long): UiEntityOfBadge? {
        val itemEntity: UiEntityOfNavigationItem = itemEntitiesById[id] ?: return null
        return itemEntity.badgeEntity
    }

    override fun editBadgeNumber(id: Long, badgeNumber: Int?) {
        val badgeEntity: UiEntityOfBadge = findBadgeEntity(id) ?: return
        badgeEntity.label = null
        badgeEntity.number = badgeNumber
    }

    override fun editBadgeVisibility(id: Long, isVisible: Boolean) {
        val badgeEntity: UiEntityOfBadge = findBadgeEntity(id) ?: return
        badgeEntity.isVisible = isVisible
    }
}
