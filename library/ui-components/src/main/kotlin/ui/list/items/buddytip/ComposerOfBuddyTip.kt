package pe.com.scotiabank.blpm.android.ui.list.items.buddytip

import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

class ComposerOfBuddyTip(private val collector: CollectorOfBuddyTip) {

    var entity: UiEntityOfBuddyTip? = null
        private set

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        callback: Runnable? = null,
    ): UiCompound<UiEntityOfBuddyTip> {

        val entities: List<UiEntityOfBuddyTip> = collector.collect(
            paddingEntity = paddingEntity,
            callback = callback,
        )
        entity = entities.firstOrNull()
        val adapterFactory = AdapterFactoryOfBuddyTip()

        return UiCompound(entities, adapterFactory)
    }
}
