package pe.com.scotiabank.blpm.android.client.dashboard.home

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.AdapterFactoryOfQuickActionCard
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.UiEntityOfQuickActionCard
import java.lang.ref.WeakReference

class ComposerOfGateProduct(
    appModel: AppModel,
    paddingEntity: UiEntityOfPadding,
    receiver: InstanceReceiver,
    weakResources: WeakReference<Resources?>,
) {

    private val converter by lazy {
        ConverterForGateProduct(
            appModel = appModel,
            paddingEntity = paddingEntity,
            receiver = receiver,
            weakResources = weakResources,
        )
    }

    private val itemEntities: MutableList<UiEntityOfQuickActionCard<GateProduct>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfQuickActionCard<GateProduct>> {
        val adapterFactory: AdapterFactoryOfQuickActionCard<GateProduct> =
            AdapterFactoryOfQuickActionCard()

        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }


    fun add(data: GateProduct) {
        val newEntity: UiEntityOfQuickActionCard<GateProduct> = converter.toUiEntityOfQuickActionCard(
            data = data,
        )
        itemEntities.add(newEntity)
    }
}
