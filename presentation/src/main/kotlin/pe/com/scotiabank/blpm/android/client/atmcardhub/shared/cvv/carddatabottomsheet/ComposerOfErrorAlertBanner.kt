package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.AdapterFactoryOfAlertBanner
import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.UiEntityOfAlertBanner

class ComposerOfErrorAlertBanner(
    private val converter: ConverterOfErrorAlertBanner
) : AlertBannerService {

    private val itemEntities: MutableList<UiEntityOfAlertBanner<Any>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfAlertBanner<Any>> {

        val adapterFactory: AdapterFactoryOfAlertBanner<Any> = AdapterFactoryOfAlertBanner()

        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    override fun addAlertBanner(alertBannerInfo: AlertBannerInfo) {
        itemEntities.clear()
        if (AlertBannerInfo.NONE == alertBannerInfo) return

        val entity: UiEntityOfAlertBanner<Any> = converter.toUiEntity(alertBannerInfo)
        itemEntities.add(entity)
    }

    override fun findAlertBannerInfoBy(id: Long): AlertBannerInfo? {
        return itemEntities.firstOrNull { entity -> entity.id == id }?.data as? AlertBannerInfo
    }
}
