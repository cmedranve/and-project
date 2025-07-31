package pe.com.scotiabank.blpm.android.ui.list.items.image.qr

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfQr(private val converter: ConverterForQr) : QrService {

    private val entities: MutableList<UiEntityOfQr> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfQr> {

        val adapterFactory = AdapterFactoryOfQr()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun clearThenAddQr(rawQr: String) {
        val newEntities: UiEntityOfQr = converter.toUiEntityOfQr(rawQr = rawQr)
        entities.clear()
        entities.add(newEntities)
    }
}
