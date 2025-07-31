package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.AdapterFactoryOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard

class ComposerOfCardSetting(private val converter: ConverterOfCardSetting): CardService {

    val itemEntities : MutableList<UiEntityOfCard<Setting>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCard<Setting>> {

        val adapterFactory: AdapterFactoryOfCard<Setting> = AdapterFactoryOfCard()
        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    override fun addCard(setting: Setting) {

        val newEntity: UiEntityOfCard<Setting> = converter.toUiEntity(
            setting = setting,
            id = setting.info.cardId,
        )
        itemEntities.add(newEntity)
    }

    override fun editCard(switchId: Long, isChecked: Boolean) {
        val indexOfOldEntity: Int = itemEntities.indexOfFirst { entity ->
            isMatching(entity.data, switchId)
        }
        if (NOT_FOUND == indexOfOldEntity) return

        val oldEntity = itemEntities[indexOfOldEntity]

        val setting: Setting = oldEntity.data ?: return
        setting.setIsChecked(isChecked)

        val newEntity: UiEntityOfCard<Setting> = converter.toUiEntity(
            setting = setting,
            id = oldEntity.id,
        )
        itemEntities[indexOfOldEntity] = newEntity
    }

    private fun isMatching(
        underEvaluation: Setting?,
        targetId: Long,
    ): Boolean = underEvaluation?.info?.switchId == targetId

    override fun updateCardsAfterLocking(isLocked: Boolean) = itemEntities
        .filterNot(::isLockingCard)
        .forEach { card -> updateCardAfterLocking(card, isLocked) }

    private fun isLockingCard(
        card: UiEntityOfCard<Setting>,
    ): Boolean = card.id == CardSettingInfo.TEMPORARILY_LOCKING.cardId

    private fun updateCardAfterLocking(card: UiEntityOfCard<Setting>, isLocked: Boolean) {
        val setting: Setting = card.data ?: return
        setting.updateSettingAfterLocking(isLocked)
        editCard(switchId = setting.info.switchId, isChecked = setting.isChecked)
    }

    override fun clearCards() {
        itemEntities.clear()
    }

    companion object {

        private val NOT_FOUND: Int
            get() = -1
    }
}