package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.AdapterFactoryOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip
import kotlin.time.Duration

class ComposerOfTimerBuddyTip(
    private val converter: ConverterOfTimerBuddyTip,
) : TimerBuddyTipService {

    private val itemEntities: MutableList<UiEntityOfBuddyTip> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfBuddyTip> {
        val adapterFactory = AdapterFactoryOfBuddyTip()
        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    override fun addTimerBuddyTip(timerInfo: TimerInfo , duration: Duration?) {
        val entity: UiEntityOfBuddyTip = converter.toUiEntity(timerInfo, duration)
        itemEntities.clear()
        itemEntities.add(entity)
    }
}
